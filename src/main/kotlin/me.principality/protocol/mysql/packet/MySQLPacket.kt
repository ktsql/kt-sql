package me.principality.protocol.mysql.packet

/**
 * https://mariadb.com/kb/en/library/0-packet/
 */
interface MySQLPacket {
    companion object {
        const val PAYLOAD_LENGTH: Int = 3
        const val SEQUENCE_LENGTH: Int = 1
    }

    fun getSequenceId(): Int

    fun getPacketSize(): Int

    fun writeTo(payload: MySQLPacketPayload) : MySQLPacketPayload
}