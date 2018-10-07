package me.principality.ktsql.sqlexec.hbase

import org.apache.calcite.jdbc.Driver

/**
 * 继承Driver，对逻辑进行重写
 */
class SqlDriver : Driver {
    val CONNECT_STRING_PREFIX = "jdbc:ktsql:"

    constructor() {
        Driver()
        // 下面开始做一些自己的初始化工作
    }

    /**
     * 这个是一定要继承的，因为在一开始被UnregisteredDriver.connect(url, info)调用
     */
    override fun getConnectStringPrefix(): String {
        return CONNECT_STRING_PREFIX
    }

    // 把自己注册到jdbc driver代码中
    companion object {
        init {
            SqlDriver().register()
        }
    }
}