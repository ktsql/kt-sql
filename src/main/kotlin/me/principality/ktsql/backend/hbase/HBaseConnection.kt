package me.principality.ktsql.backend.hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.HConstants
import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.ConnectionFactory

/**
 * 统一管理HBaseConnection，避免重复创建
 */
object HBaseConnection {
    private var isInit = false
    private var connection: Connection? = null

    fun init(operand: MutableMap<String, Any>?) {
        val config = HBaseConfiguration.create()
        val zkquorum: String = operand?.get("zkclient").toString()
        config.set(HConstants.ZOOKEEPER_QUORUM, zkquorum)

        isInit = true
        connection = ConnectionFactory.createConnection(config)
    }

    fun connection(): Connection {
        if (!isInit)
            throw RuntimeException("call connection before init")

        return connection!!
    }

    fun close() {
        if (isInit) {
            connection!!.close()
            isInit = false
        }
    }
}