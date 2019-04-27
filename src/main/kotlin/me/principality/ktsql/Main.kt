package me.principality.ktsql

import io.vertx.core.Vertx
import me.principality.ktsql.protocol.TcpServer
import me.principality.ktsql.protocol.mysql.MySQLHandler

open class Main {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val server = TcpServer(Vertx.vertx(), "0.0.0.0", 30000)
            Runtime.getRuntime().addShutdownHook(Thread { server.stop() })
            server.start(MySQLHandler())
        }
    }
}
