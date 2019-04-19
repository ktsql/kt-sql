package me.principality.ktsql.protocol.mysql

import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import me.principality.ktsql.sqlexec.SqlExecuteHandler
import me.principality.ktsql.protocol.mysql.helper.AuthorityHelper
import me.principality.ktsql.protocol.mysql.helper.ConnectionIdGenerator
import me.principality.ktsql.protocol.mysql.helper.MySQLSessionCache
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.command.CommandPacket
import me.principality.ktsql.protocol.mysql.packet.command.CommandPacketFactory
import me.principality.ktsql.protocol.mysql.packet.command.query.QueryCommandPacket
import me.principality.ktsql.protocol.mysql.packet.constant.ServerErrorCode
import me.principality.ktsql.protocol.mysql.packet.generic.EofPacket
import me.principality.ktsql.protocol.mysql.packet.generic.ErrPacket
import me.principality.ktsql.protocol.mysql.packet.generic.OkPacket
import me.principality.ktsql.protocol.mysql.packet.handshake.HandshakePacket
import me.principality.ktsql.protocol.mysql.packet.handshake.HandshakeResponse41Packet
import mu.KotlinLogging
import java.sql.SQLException

/**
 * MysqlHandler负责命令的解析与执行，保存了命令处理的上下文
 *
 * 考虑以下要点：
 * 1. 单一连接多个命令，在事务处理时存在关联关系，上下文需要关注命令的串行
 * 2. 实现连接的状态管理：连接时握手，接收包时先认证，然后对命令进行处理
 */
class MySQLHandler : Handler<NetSocket> {
    private val logger = KotlinLogging.logger {}
    private var isAuthorized: Boolean = false
    private val authorityHelper = AuthorityHelper()
    private lateinit var remoteSocket: NetSocket
    private var currentSequenceId: Int = 0
    private val sqlExecHandler = SqlExecuteHandler()

    override fun handle(socket: NetSocket?) {
        logger.info("${socket?.remoteAddress() ?: "invalid socket"}")
        if (socket != null) {
            remoteSocket = socket
            handshake(socket)
            socket.handler { inBuffer ->
                logger.debug("incoming data: " + inBuffer.toString())
                if (!isAuthorized) {
                    authorize(inBuffer)
                } else {
                    handleCommand(inBuffer)
                }
            }
            socket.closeHandler {
                logger.debug { "remote socket ${remoteSocket} close" }
            }
        }
    }

    private fun handshake(socket: NetSocket) {
        val id = ConnectionIdGenerator.nextId()
        MySQLSessionCache.putConnection(socket.writeHandlerID(), id)
        val handshake = HandshakePacket(id, authorityHelper.authPluginData)
        socket.write(handshake.transferTo(MySQLPacketPayload(handshake.getPacketSize(), handshake.getSequenceId())).byteBuffer)
    }

    // TODO need to release payload? think about vertx.buffer carefully
    private fun authorize(buffer: Buffer) {
        val payload = MySQLPacketPayload(buffer)
        val response41 = HandshakeResponse41Packet(payload)
        if (authorityHelper.login(response41.username, response41.authResponse!!)) {
            val okPacket = OkPacket(response41.getSequenceId() + 1)
            val okPayload = MySQLPacketPayload(okPacket.getPacketSize(), response41.getSequenceId() + 1)
            remoteSocket.write(okPacket.transferTo(okPayload).byteBuffer)
            isAuthorized = true
        } else {
            // TODO localhost should replace to real ip address?
            val message = String.format(ServerErrorCode.ER_ACCESS_DENIED_ERROR.reason,
                    response41.username,
                    "localhost",
                    if (0 == response41.authResponse?.size ?: 0) "NO" else "YES")
            val errPacket = ErrPacket(response41.getSequenceId() + 1,
                    ServerErrorCode.ER_ACCESS_DENIED_ERROR,
                    message)
            val errPayload = MySQLPacketPayload(errPacket.getPacketSize(), response41.getSequenceId() + 1)
            remoteSocket.write(errPacket.transferTo(errPayload).byteBuffer)
        }
    }

    private fun handleCommand(buffer: Buffer) {
        /**
         * 根据传过来的命令，进行命令分发
         */
        fun getCommandPakcet(payload: MySQLPacketPayload, sqlPackerHandler: SqlExecuteHandler): CommandPacket {
            val packetSize = payload.readInt3()
            val sequenceId = payload.readInt1()
            val connectionId = MySQLSessionCache.getConnection(remoteSocket.writeHandlerID())
            return CommandPacketFactory.createCommandPacket(sequenceId, connectionId, payload, sqlPackerHandler)
        }

        try {
            val payload = MySQLPacketPayload(buffer)
            val packet = getCommandPakcet(payload, sqlExecHandler)
            val responses = packet.execute(sqlExecHandler)
            if (!responses.isPresent) {
                return // 这里按理不应该有空的情况，是否应该抛异常？
            }
            for (each in responses.get().packets) {
                remoteSocket.write(each.transferTo(MySQLPacketPayload(each.getPacketSize(), packet.getSequenceId())).byteBuffer)
            }
            // 针对查询包的特殊处理
            if (packet is QueryCommandPacket
                    && responses.get().getHeaderPacket() !is OkPacket
                    && responses.get().getHeaderPacket() !is ErrPacket) {
                writeMoreResults(packet as QueryCommandPacket, responses.get().packets.size)
            }
        } catch (ex: SQLException) {
            // CHECKSTYLE:OFF
            logger.debug { ex }
            val packet = ErrPacket(++currentSequenceId, ex)
            remoteSocket.write(packet.transferTo(MySQLPacketPayload(packet.getPacketSize(), packet.getSequenceId())).byteBuffer)
        } catch (ex: Exception) {
            // CHECKSTYLE:ON
            logger.debug(ex.message, ex)// { ex }
            val packet = ErrPacket(1, ServerErrorCode.ER_STD_UNKNOWN_EXCEPTION, ex.message!!)
            remoteSocket.write(packet.transferTo(MySQLPacketPayload(packet.getPacketSize(), packet.getSequenceId())).byteBuffer)
        }
    }

    /**
     * 从查询结果中逐行读取记录，并转换为可以写往目标客户端的字节流
     */
    private fun writeMoreResults(queryCommandPacket: QueryCommandPacket, headPacketsCount: Int) {
        currentSequenceId = headPacketsCount
        while (queryCommandPacket.next()) {
            val packet = queryCommandPacket.getResultValue()
            currentSequenceId = packet.getSequenceId()
            remoteSocket.write(packet.transferTo(MySQLPacketPayload(packet.getPacketSize(), packet.getSequenceId())).byteBuffer)
        }
        val packet = EofPacket(++currentSequenceId)
        remoteSocket.write(packet.transferTo(MySQLPacketPayload(packet.getPacketSize(), packet.getSequenceId())).byteBuffer)
    }
}