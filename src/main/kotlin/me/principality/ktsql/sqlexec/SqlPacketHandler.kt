package me.principality.ktsql.sqlexec

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.ktsql.utils.config.ConfigureManager
import java.sql.DriverManager
import java.util.*

/**
 * calcite提供了几种调用的方式，这里选择本地执行的方法，即calcite jdbc
 *
 * calcite采用jdbc api对外提供访问的接口，此处为使用方法示例。
 * 虽然有calcite内部api，但使用jdbc api有助于接口的规范化。
 */
class SqlPacketHandler : PacketHandleHelper {
    private val connectionString = "jdbc:calcite:parserFactory=org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl#FACTORY"

    override fun execute(sql: String): Optional<CommandResponsePackets> {
        val info = ConfigureManager.getCalciteConfig()
        val connection = DriverManager.getConnection(connectionString, info)

        val statement = connection.createStatement()
        val sets = statement.executeQuery(sql)
        val ret = SqlUtil.toResponse(sets)
        sets.close()

        connection.close()
        return ret
    }
}