package me.principality.ktsql.protocol.mysql.packet.command

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import java.util.*

interface CommandPacket : MySQLPacket {
    fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets>
}