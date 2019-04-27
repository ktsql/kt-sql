package me.principality.ktsql.protocol.mysql.packet.command

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import java.util.*

/**
 * 设计CommandPacket的意图是，某些Command并不需要调用后端，可以直接执行并返回结果
 */
interface CommandPacket : MySQLPacket {
    fun execute(helper: PacketHandleHelper): Optional<CommandResponsePackets>
}