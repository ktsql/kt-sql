package me.principality.ktsql.protocol.mysql.packet.command

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import java.util.*

class UnsupportedCommandPacket: CommandPacket {
    private val sequenceId: Int
    private val type: CommandType

    constructor(sequenceId: Int, type: CommandType) {
        this.sequenceId = sequenceId
        this.type = type
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