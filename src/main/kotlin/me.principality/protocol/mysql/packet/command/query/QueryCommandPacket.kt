package me.principality.protocol.mysql.packet.command.query

import me.principality.protocol.mysql.packet.MySQLPacket
import me.principality.protocol.mysql.packet.command.CommandPacket
import java.sql.SQLException

interface QueryCommandPacket: CommandPacket {
    /**
     * Goto next result value.
     *
     * @return has more result value or not
     * @throws SQLException SQL exception
     */
    @Throws(SQLException::class)
    abstract operator fun next(): Boolean

    /**
     * Get result value.
     *
     * @return database packet of result value
     * @throws SQLException SQL exception
     */
    @Throws(SQLException::class)
    abstract fun getResultValue(): MySQLPacket
}