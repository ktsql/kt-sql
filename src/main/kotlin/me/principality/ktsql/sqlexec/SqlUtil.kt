package me.principality.ktsql.sqlexec

import me.principality.ktsql.backend.hbase.HBaseConnection
import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import org.apache.calcite.jdbc.SqlDriver
import java.sql.ResultSet
import java.util.*

/**
 * 实现多后端的支持是可能的，但暂时没有比HBase更好的方案，FoundationDB可能是一个选择，不过需要进一步的调研
 */
object SqlUtil {

    fun init() {
        SqlDriver.register()
    }

    fun toResponse(resultSet: ResultSet): Optional<CommandResponsePackets> {
        return Optional.empty() // fixme
    }

    fun unInit() {
        HBaseConnection.close() // 释放远端的连接
    }
}