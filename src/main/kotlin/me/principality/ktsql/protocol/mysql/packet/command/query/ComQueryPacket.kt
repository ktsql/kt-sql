package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.ktsql.protocol.mysql.packet.command.CommandType
import me.principality.ktsql.protocol.mysql.helper.SelectParamParser2
import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.generic.EofPacket
import me.principality.ktsql.sqlexec.SqlUtil
import java.util.*

/**
 * https://dev.mysql.com/doc/internals/en/com-query.html#packet-COM_QUERY
 */
class ComQueryPacket : QueryCommandPacket {
    private val sequenceId: Int
    private val sql: String
    private val sqlExecHandler: PacketHandleHelper
    private val parser: SelectParamParser2
    private val resultSetRowPackets: MutableList<TextResultSetRowPacket> = mutableListOf()
    private var resultSetIndex: Int = 0

    constructor(sequenceId: Int, connectionId: Int, payload: MySQLPacketPayload, handler: PacketHandleHelper) {
        this.sequenceId = sequenceId
        this.sql = payload.readStringEOF()
        this.sqlExecHandler = handler
        this.parser = SelectParamParser2()
    }

    /**
     * MySQL的系统参数查询，calcite并不支持，所以在这里要做特殊处理。
     */
    override fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets> {
        if (sql.contains("select", true)) {
            val result = parser.parse(sql)
            if (result.isNotEmpty()) {
                // 对select @@a as a进行处理
                // 1. 生成返回的表头CommandResponsePackets
                // 2. 把数据保存到resultSetRowPackets
                // 3. 返回处理结果，由上层逻辑把包写到目标客户端
                val columnCountPacket = ColumnCountPacket(sequenceId + 1, result.size)
                val columnDefinition41Packets: MutableList<ColumnDefinition41Packet> = mutableListOf()
                for (element in result) {
                    columnDefinition41Packets.add(createColumnDefinition41Packet(element))
                    resultSetRowPackets.add(createTextResultSetRowPacket(element))
                }
                val eofPacket = EofPacket(sequenceId + 1)
                val packets = QueryResponsePackets(columnCountPacket, columnDefinition41Packets, eofPacket)

                return Optional.of(packets)
            }
        }

        return SqlUtil.toResponse(helper.executeQuery(sql))
    }

    override fun next(): Boolean {
        // 这里实现对resultSetRowPackets结果的遍历
        if (resultSetIndex >= resultSetRowPackets.size) {
            return false
        }
        ++resultSetIndex
        return true
    }

    override fun getResultValue(): MySQLPacket {
        // 这里实现对resultSetRowPackets的值读取
        return resultSetRowPackets.get(resultSetIndex)
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return (1
                + sql.length)
    }

    override fun transferTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeInt1(CommandType.COM_QUERY.value)
        payload.writeStringEOF(sql)
        return payload
    }

    private fun createColumnDefinition41Packet(pair: Pair<String, String>): ColumnDefinition41Packet {
        TODO()
    }

    private fun createTextResultSetRowPacket(pair: Pair<String, String>): TextResultSetRowPacket {
        TODO()
    }
}