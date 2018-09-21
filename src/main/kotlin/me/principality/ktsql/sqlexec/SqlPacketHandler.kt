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
    override fun execute(sql: String): Optional<CommandResponsePackets> {
        val info = ConfigureManager.getCalciteConfig()
        val connection = DriverManager.getConnection("jdbc:calcite:", info)
        val tables = connection.metaData.getTables(null, null, null, null)
        val ret = SqlUtil.toResponse(tables)
        tables.close()
        connection.close()
        return ret
    }
    /*
        Class.forName("org.apache.calcite.jdbc.Driver")

    // meta 初始化，将来通过统一的meta服务获得
    val inputStream = getClass().getClassLoader().getResourceAsStream("model.json")
    val info = new Properties
    info.setProperty("model", Source.fromInputStream(inputStream).mkString) // 传入文件路径，或直接inline:配置内容

    // 连接管理及调用，这里调用calcite，calcite后端实现通过连接池进行优化
    val connection = DriverManager.getConnection("jdbc:calcite:", info)
    connection
     */
}