package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.helper.SelectParamParser2
import me.principality.ktsql.protocol.mysql.helper.SystemVariables
import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.ktsql.protocol.mysql.packet.command.CommandType
import me.principality.ktsql.protocol.mysql.packet.command.QueryResponsePackets
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
     * https://dev.mysql.com/doc/internals/en/com-query-response.html#packet-COM_QUERY_Response
     *
     * The query-response packet is a meta packet which can be one of:
     * - ERR_Packet
     * - OK_Packet
     * - Protocol::LOCAL_INFILE_Request
     * - ProtocolText::Resultset
     *
     * MySQL的系统参数查询，calcite并不支持，所以在这里要做特殊处理。
     */
    override fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets> {
        if (sql.contains("select", true) && sql.contains("@@")) {
            val result = parser.parse(sql)
            // 这里按理来说，不应该有查询不出来正确结果的私情
            if (result.isNotEmpty()) {
                // 对select @@a as a进行处理
                // 1. 生成返回的表头CommandResponsePackets
                // 2. 把数据保存到resultSetRowPackets
                // 3. 返回处理结果，由上层逻辑把包写到目标客户端
                val columnCountPacket = ColumnCountPacket(sequenceId + 1, result.size)
                val columnDefinition41Packets: MutableList<ColumnDefinition41Packet> = mutableListOf()
                var columnDefSeqNo = sequenceId + 1
                for (element in result) {
                    columnDefSeqNo++
                    columnDefinition41Packets.add(createColumnDefinition41Packet(columnDefSeqNo, element))
                }
                var txtResultSetRowSeqNo = columnDefSeqNo
                for (element in result) {
                    txtResultSetRowSeqNo++
                    resultSetRowPackets.add(createTextResultSetRowPacket(txtResultSetRowSeqNo, element))
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

    private fun createColumnDefinition41Packet(seqNo: Int, pair: Pair<String, String>): ColumnDefinition41Packet {
        if (SystemVariables.keyValueMap.containsKey(pair.first)) {
            val length = SystemVariables.keyLengthMap[pair.first] ?: throw IllegalArgumentException()
            val type = SystemVariables.keyTypeMap[pair.first] ?: throw IllegalArgumentException()

            return ColumnDefinition41Packet(seqNo,
                    "", // schema: String
                    "", // table: String
                    "", // orgTable: String
                    pair.second, // name: String
                    pair.first, // orgName: String
                    length, // columnLength: Int
                    type, // columnType: ColumnType
                    0 // decimals: Int
            )
        }
        throw IllegalArgumentException()
    }

    private fun createTextResultSetRowPacket(seqNo: Int, pair: Pair<String, String>): TextResultSetRowPacket {
        val value = SystemVariables.getValue(pair.first)
        if (value != null) {
            return TextResultSetRowPacket(seqNo, value.toString())
        }
        throw IllegalArgumentException()
    }
}