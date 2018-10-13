package me.principality.ktsql.protocol.mysql.helper

import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import java.util.*

/**
 * 定义命令实现的接口。每一个实现了该接口的工具类，都可用于命令的实际执行。
 */
interface PacketHandleHelper {
    fun executeQuery(sql: String): Optional<CommandResponsePackets>
    fun executeDdl(sql: String): Optional<CommandResponsePackets>
    fun execute(sql: String): Optional<CommandResponsePackets>
}