package me.principality.ktsql.sqlexec

import me.principality.ktsql.protocol.mysql.helper.PacketHandleHelper
import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.ktsql.utils.config.ConfigureProvider
import mu.KotlinLogging
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*

/**
 * calcite提供了几种调用的方式，这里选择本地执行的方法，即calcite jdbc
 *
 * calcite采用jdbc api对外提供访问的接口，此处为使用方法示例。
 * 虽然有calcite内部api，但使用jdbc api有助于接口的规范化。
 *
 * 到了这里的sql，都是Calcite可以处理的SQL，那些需要特殊支持的都已经被过滤掉了
 */
class SqlExecuteHandler : PacketHandleHelper {
    private val logger = KotlinLogging.logger {}
    private val connectionString = "jdbc:calcite:parserFactory=org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl#FACTORY"
    private val info = ConfigureProvider.getCalciteConfig()
    private val connection = DriverManager.getConnection(connectionString, info) // statement公用，不要关闭

    override fun executeQuery(sql: String): ResultSet {
        val statement = connection.createStatement()
        val sets = statement.executeQuery(sql)
        statement.close()
        return sets
    }

    override fun executeDdl(sql: String): Int {
        val statement = connection.createStatement()
        val ret = statement.executeUpdate(sql)
        statement.close()
        return ret
    }

    override fun execute(sql: String): Boolean {
        val statement = connection.createStatement()
        val ret = statement.execute(sql)
        statement.close()
        return ret
    }

    override fun close() {
        // todo statment到底应该是在一个session里面共用，还是应该每个sql生成一个？
    }
}