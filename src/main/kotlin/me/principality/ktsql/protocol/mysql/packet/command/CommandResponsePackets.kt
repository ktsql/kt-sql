package me.principality.ktsql.protocol.mysql.packet.command

import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import java.util.*

/**
 * 通用的命令指令执行结果父类
 *
 * 命令本身就意味着，该类是一个集合类，自己独立管理需返回的包列表信息
 */
open class CommandResponsePackets {
    val packets = LinkedList<MySQLPacket>()

    constructor() {}

    constructor(packet: MySQLPacket) {
        packets.add(packet)
    }

    fun getHeaderPacket(): MySQLPacket {
        return packets.first
    }
}