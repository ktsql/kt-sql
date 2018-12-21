package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.command.CommandPacket
import java.sql.SQLException

/**
 * 对查询结果做特殊的处理，实现查询结果的缓存和遍历
 */
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