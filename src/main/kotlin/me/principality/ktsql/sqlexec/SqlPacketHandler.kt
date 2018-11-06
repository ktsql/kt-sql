package me.principality.ktsql.sqlexec

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.ktsql.utils.config.ConfigureProvider
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
    private val info = ConfigureProvider.getCalciteConfig()
    private val connection = DriverManager.getConnection(connectionString, info)

    override fun executeQuery(sql: String): Optional<CommandResponsePackets> {
        val statement = connection.createStatement()
        val sets = statement.executeQuery(sql)
        // fixme 仅用于测试
        while (sets.next()) {
            println(sets.getString("rowkey"))
        }
        val ret = SqlUtil.toResponse(sets)
        sets.close()

        statement.close()
        return ret
    }

    override fun executeDdl(sql: String): Optional<CommandResponsePackets> {
        val statement = connection.createStatement()
        val sets = statement.executeUpdate(sql)

        statement.close()
        return Optional.empty()
    }

    override fun execute(sql: String): Optional<CommandResponsePackets> {
        val statement = connection.createStatement()
        val sets = statement.execute(sql)
        /*
        val ret = SqlUtil.toResponse(sets)
        sets.close()
        */

        statement.close()
        return Optional.empty()
    }

    override fun close() {
        if (!connection.isClosed) {
            connection.close()
        }
    }
}