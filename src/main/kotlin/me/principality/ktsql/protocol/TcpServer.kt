package me.principality.ktsql.protocol

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.core.net.NetServerOptions
import me.principality.ktsql.backend.hbase.HBaseConnection
import me.principality.ktsql.sqlexec.SqlUtil
import mu.KotlinLogging

/**
 * 关于资源管理：
 * 1. Tcp服务器和与HBase的连接，在ktsql都是唯一的，在服务起来的时候初始化，关闭时释放
 * 2. statement, handler是每一个连接都会创建的，是属于session级别的资源，在远端关闭的时候进行清理
 */
class TcpServer(vx: Vertx, host: String, port: Int) {
    private val globalVertx = vx
    private val options = NetServerOptions(host = host, port = port)
    private val server = globalVertx.createNetServer(options)!!
    private val logger = KotlinLogging.logger {}

    fun start(handler: Handler<NetSocket>) {
        SqlUtil.init()

        server.connectHandler(handler)
        server.exceptionHandler { exception ->
            logger.debug { exception.stackTrace }
        }
        server.listen { result ->
            if (result.succeeded()) {
                logger.info { "server started" }
            } else {
                logger.info { "server starting failed" }
            }
        }
    }

    fun stop() {
        server.close { res ->
            if (res.succeeded()) {
                logger.info { "server stopped" }
            } else {
                logger.info { "server can not be closed" }
            }
        }

        HBaseConnection.close() // 释放远端的连接
    }
}