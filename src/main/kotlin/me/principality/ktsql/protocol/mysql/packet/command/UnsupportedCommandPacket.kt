package me.principality.ktsql.protocol.mysql.packet.command

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.constant.ServerErrorCode
import me.principality.ktsql.protocol.mysql.packet.generic.ErrPacket
import java.util.*

/**
 * 所有的不支持的命令，都生成不支持的packet
 */
class UnsupportedCommandPacket : CommandPacket {
    private val sequenceId: Int
    private val type: CommandType

    constructor(sequenceId: Int, type: CommandType) {
        this.sequenceId = sequenceId
        this.type = type
    }

    override fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets> {
        val message = String.format(ServerErrorCode.ER_UNSUPPORTED_COMMAND.reason, type.toString())
        return Optional.of(CommandResponsePackets(
                ErrPacket(getSequenceId() + 1, ServerErrorCode.ER_UNSUPPORTED_COMMAND, message)))
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        TODO("not implemented")
    }

    override fun transferTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        TODO("not implemented")
    }
}