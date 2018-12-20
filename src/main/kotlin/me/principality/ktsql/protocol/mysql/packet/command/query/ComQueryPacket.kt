package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.command.CommandPacket
import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.ktsql.protocol.mysql.packet.command.CommandType
import me.principality.ktsql.protocol.mysql.helper.SelectParamParser2
import java.util.*

class ComQueryPacket : CommandPacket {
    private val sequenceId: Int
    private val sql: String
    private val sqlexecHandler: PacketHandleHelper
    private val parser: SelectParamParser2

    constructor(sequenceId: Int, connectionId: Int, payload: MySQLPacketPayload, handler: PacketHandleHelper) {
        this.sequenceId = sequenceId
        this.sql = payload.readStringEOF()
        this.sqlexecHandler = handler
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
            }
        }

        return helper.executeQuery(sql)
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
}