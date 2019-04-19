package me.principality.ktsql.protocol.mysql.packet.command

import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import java.util.*

/**
 *
 */
open class CommandResponsePackets {
    val packets = LinkedList<MySQLPacket>()

    constructor() {}

    constructor(packet: MySQLPacket) {
        packets.add(packet)
    }

    fun getHeaderPacket(): MySQLPacket {
        return packets.iterator().next()
    }
}