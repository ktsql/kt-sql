package me.principality.protocol.mysql

import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import me.principality.protocol.mysql.helper.AuthorityHelper
import me.principality.protocol.mysql.helper.ConnectionIdGenerator
import me.principality.protocol.mysql.helper.MySQLSessionCache
import me.principality.protocol.mysql.helper.PacketHandleHelper
import me.principality.protocol.mysql.packet.MySQLPacketPayload
import me.principality.protocol.mysql.packet.command.CommandPacket
import me.principality.protocol.mysql.packet.command.CommandPacketFactory
import me.principality.protocol.mysql.packet.command.query.QueryCommandPacket
import me.principality.protocol.mysql.packet.constant.ServerErrorCode
import me.principality.protocol.mysql.packet.generic.EofPacket
import me.principality.protocol.mysql.packet.generic.ErrPacket
import me.principality.protocol.mysql.packet.generic.OkPacket
import me.principality.protocol.mysql.packet.handshake.HandshakePacket
import me.principality.protocol.mysql.packet.handshake.HandshakeResponse41Packet
import me.principality.sqlrewriter.SqlRewriterPacketHandler
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
    private var remoteSocket: NetSocket? = null
    private var currentSequenceId: Int = 0

    override fun handle(socket: NetSocket?) {
        logger.info { "${socket?.remoteAddress() ?: "invalid socket"}" }
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
        }
    }

    private fun handshake(socket: NetSocket) {
        val id = ConnectionIdGenerator.nextId()
        MySQLSessionCache.putConnection(socket.writeHandlerID(), id)
        val handshake = HandshakePacket(id, authorityHelper.authPluginData)
        socket.write(handshake.writeTo(MySQLPacketPayload(handshake.getPacketSize())).byteBuffer)
    }

    // TODO need to release payload? think about vertx.buffer carefully
    private fun authorize(buffer: Buffer) {
        val payload = MySQLPacketPayload(buffer)
        val response41 = HandshakeResponse41Packet(payload)
        if (authorityHelper.login(response41.username, response41.authResponse!!)) {
            val packet = OkPacket(response41.getSequenceId() + 1)
            remoteSocket?.write(packet.writeTo(MySQLPacketPayload(packet.getPacketSize())).byteBuffer)
            isAuthorized = true
        } else {
            // TODO localhost should replace to real ip address
            val packet = ErrPacket(response41.getSequenceId() + 1,
                    ServerErrorCode.ER_ACCESS_DENIED_ERROR, response41.username,
                    "localhost",
                    if (0 == response41.authResponse?.size ?: 0) "NO" else "YES")
            remoteSocket?.write(packet.writeTo(MySQLPacketPayload(packet.getPacketSize())).byteBuffer)
        }
    }

    private fun handleCommand(buffer: Buffer) {
        try {
            val payload = MySQLPacketPayload(buffer)
            val packet = getCommandPakcet(payload)
            val responses = packet.execute(SqlRewriterPacketHandler())
            if (!responses.isPresent()) {
                return
            }
            for (each in responses.get().packets) {
                remoteSocket?.write(each.writeTo(MySQLPacketPayload(each.getPacketSize())).byteBuffer)
            }
            if (packet is QueryCommandPacket
                    && responses.get().getHeaderPacket() !is OkPacket
                    && responses.get().getHeaderPacket() !is ErrPacket) {
                writeMoreResults(packet as QueryCommandPacket, responses.get().packets.size)
            }
        } catch (ex: SQLException) {
            val packet = ErrPacket(++currentSequenceId, ex)
            remoteSocket?.write(packet.writeTo(MySQLPacketPayload(packet.getPacketSize())).byteBuffer)
            // CHECKSTYLE:OFF
        } catch (ex: Exception) {
            // CHECKSTYLE:ON
            val packet = ErrPacket(1, ServerErrorCode.ER_STD_UNKNOWN_EXCEPTION, ex.message!!)
            remoteSocket?.write(packet.writeTo(MySQLPacketPayload(packet.getPacketSize())).byteBuffer)
        }
    }

    private fun getCommandPakcet(payload: MySQLPacketPayload): CommandPacket {
        val sequenceId = payload.readInt1()
        val connectionId = MySQLSessionCache.getConnection(remoteSocket!!.writeHandlerID())
        return CommandPacketFactory.createCommandPacket(sequenceId, connectionId, payload)
    }

    private fun writeMoreResults(queryCommandPacket: QueryCommandPacket, headPacketsCount: Int) {
        currentSequenceId = headPacketsCount
        while (queryCommandPacket.next()) {
            val packet = queryCommandPacket.getResultValue()
            currentSequenceId = packet.getSequenceId()
            remoteSocket?.write(packet.writeTo(MySQLPacketPayload(packet.getPacketSize())).byteBuffer)
        }
        val packet = EofPacket(++currentSequenceId)
        remoteSocket?.write(packet.writeTo(MySQLPacketPayload(packet.getPacketSize())).byteBuffer)
    }

}