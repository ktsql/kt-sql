package me.principality.ktsql.sqlexec

import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import java.sql.ResultSet
import java.util.*

object SqlUtil {
    fun toResponse(resultSet: ResultSet): Optional<CommandResponsePackets> {
        return Optional.empty() // fixme
    }
}