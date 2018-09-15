package me.principality.protocol

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.core.net.NetServerOptions
import mu.KotlinLogging

class TcpServer(vx: Vertx, host: String, port: Int) {
    private val globalVertx = vx
    private val options = NetServerOptions(host = host, port = port)
    private val server = globalVertx.createNetServer(options)!!
    private val logger = KotlinLogging.logger {}

    fun start(handler: Handler<NetSocket>) {
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
    }
}