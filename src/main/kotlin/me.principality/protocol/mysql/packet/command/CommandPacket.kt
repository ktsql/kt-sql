package me.principality.protocol.mysql.packet.command

import me.principality.protocol.mysql.helper.PacketHandleHelper
import me.principality.protocol.mysql.packet.MySQLPacket
import java.util.*

interface CommandPacket : MySQLPacket {
    fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets>
}