package me.principality.protocol.mysql.packet.constant

enum class PacketHeader(val value: Int) {
    OK(0x00),
    EOF(0xfe),
    ERR(0xff);
}