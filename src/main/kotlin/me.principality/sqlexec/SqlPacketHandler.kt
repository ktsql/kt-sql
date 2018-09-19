package me.principality.sqlexec

import me.principality.protocol.mysql.helper.PacketHandleHelper
import me.principality.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.utils.config.ConfigureManager
import java.sql.DriverManager
import java.util.*

/**
 * calcite提供了几种调用的方式，这里选择本地执行的方法，即calcite jdbc
 */
class SqlPacketHandler : PacketHandleHelper {
    override fun execute(sql: String): Optional<CommandResponsePackets> {
        val info = ConfigureManager.getCalciteConfig()
        val connection = DriverManager.getConnection("jdbc:calcite:", info)
        val tables = connection.metaData.getTables(null, null, null, null)
        val ret = SqlUtil.toResponse(tables)
        tables.close()
        connection.close()
        return ret
    }
}