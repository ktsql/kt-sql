package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.command.CommandPacket
import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import java.util.*

class ComStmtExecutePacket: CommandPacket {
    constructor(sequenceId: Int, connectionId: Int, payload: MySQLPacketPayload, handler: PacketHandleHelper) {

    }

    override fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets> {
        TODO("not implemented")
    }

    override fun getSequenceId(): Int {
        TODO("not implemented")
    }

    override fun getPacketSize(): Int {
        TODO("not implemented")
    }

    override fun transferTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        TODO("not implemented")
    }
}