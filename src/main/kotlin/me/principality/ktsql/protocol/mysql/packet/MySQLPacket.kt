package me.principality.ktsql.protocol.mysql.packet

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

    fun transferTo(payload: MySQLPacketPayload) : MySQLPacketPayload

    fun intLenenc(value: Long): Int {
        if (value < 251) {
            return 1
        }
        if (value < Math.pow(2.0, 16.0)) {
            return 3
        }
        if (value < Math.pow(2.0, 24.0)) {
            return 5
        }
        return 9
    }
}