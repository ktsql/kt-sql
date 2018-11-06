package me.principality.ktsql.sqlexec

import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import org.apache.calcite.jdbc.SqlDriver
import java.sql.ResultSet
import java.util.*

object SqlUtil {

    fun init() {
        SqlDriver.register()
    }

    fun toResponse(resultSet: ResultSet): Optional<CommandResponsePackets> {
        return Optional.empty() // fixme
    }
}