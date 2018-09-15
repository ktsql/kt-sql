package me.principality.protocol.mysql.packet.command.query

import me.principality.protocol.mysql.helper.PacketHandleHelper
import me.principality.protocol.mysql.packet.MySQLPacketPayload
import me.principality.protocol.mysql.packet.command.CommandPacket
import me.principality.protocol.mysql.packet.command.CommandResponsePackets
import java.util.*

class ComStmtClosePacket: CommandPacket {
    constructor(sequenceId: Int, payload: MySQLPacketPayload) {

    }

    override fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSequenceId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPacketSize(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}