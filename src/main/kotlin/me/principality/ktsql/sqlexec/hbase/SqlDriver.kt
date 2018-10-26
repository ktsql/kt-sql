package me.principality.ktsql.sqlexec.hbase

import org.apache.calcite.avatica.ConnectStringParser
import org.apache.calcite.avatica.UnregisteredDriver
import org.apache.calcite.jdbc.Driver
import java.sql.Connection
import java.sql.SQLException
import java.util.*

/**
 * 继承Driver，对逻辑进行重写
 */
class SqlDriver() : Driver() {
    val CONNECT_STRING_PREFIX = "jdbc:ktsql:"

    init {
        Driver()

        // 下面开始做一些自己的初始化工作
    }

    // 把自己注册到jdbc driver代码中，这里是延迟加载，还需要优化
    companion object {
        init {
            SqlDriver().register()
        }
    }

    /**
     * 这个是一定要继承的，因为在一开始被UnregisteredDriver.connect(url, info)调用
     */
    override fun getConnectStringPrefix(): String {
        return CONNECT_STRING_PREFIX
    }

    override fun getFactoryClassName(jdbcVersion: UnregisteredDriver.JdbcVersion): String {
        when (jdbcVersion) {
            UnregisteredDriver.JdbcVersion.JDBC_30,
            UnregisteredDriver.JdbcVersion.JDBC_40 ->
                throw IllegalArgumentException("JDBC version not supported: $jdbcVersion")
            UnregisteredDriver.JdbcVersion.JDBC_41 ->
                return "me.principality.ktsql.sqlexec.hbase.SqlFactory"
            else ->
                return "me.principality.ktsql.sqlexec.hbase.SqlFactory"
        }
    }

    /**
     * 对connect进行重写，实现自己的连接逻辑：定制的factory
     */
    @Throws(SQLException::class)
    override fun connect(url: String, info: Properties): Connection? {
        if (!this.acceptsURL(url)) {
            return null
        } else {
            val prefix = this.connectStringPrefix

            assert(url.startsWith(prefix))

            val urlSuffix = url.substring(prefix.length)
            val info2 = ConnectStringParser.parse(urlSuffix, info)
            val connection = this.factory.newConnection(this, this.factory, url, info2)
            this.handler.onConnectionInit(connection)
            return connection
        }
    }
}