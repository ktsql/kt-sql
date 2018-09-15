package me.principality

import io.vertx.core.Vertx
import me.principality.protocol.TcpServer
import me.principality.protocol.mysql.MySQLHandler

open class Main {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val server = TcpServer(Vertx.vertx(),"0.0.0.0", 30000)
            Runtime.getRuntime().addShutdownHook(Thread { server.stop() })
            server.start(MySQLHandler())
        }
    }
}
