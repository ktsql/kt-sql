package me.principality.protocol.mysql.packet.command.query

import me.principality.protocol.mysql.helper.PacketHandleHelper
import me.principality.protocol.mysql.packet.MySQLPacketPayload
import me.principality.protocol.mysql.packet.command.CommandPacket
import me.principality.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.protocol.mysql.packet.command.CommandType
import java.util.*

class ComQueryPacket: CommandPacket {
    private val sequenceId: Int
    private val sql: String
    private val sqlexecHandler: PacketHandleHelper

    constructor(sequenceId: Int, connectionId: Int, payload: MySQLPacketPayload, handler: PacketHandleHelper) {
        this.sequenceId = sequenceId
        this.sql = payload.readStringEOF()
        this.sqlexecHandler = handler
    }

    override fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets> {
        TODO("not implemented")

        return helper.execute(sql)
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return (1
                + sql.length)
    }

    override fun writeTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeInt1(CommandType.COM_QUERY.value)
        payload.writeStringEOF(sql)
        return payload
    }
}