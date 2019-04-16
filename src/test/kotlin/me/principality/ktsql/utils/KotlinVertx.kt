package me.principality.ktsql.utils

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.kotlin.core.VertxOptions

/**
 * 对底层的工具类进行测试
 */
class KotlinVertx {

    var vertx: Vertx

    init {
        vertx = Vertx.vertx(VertxOptions(workerPoolSize = 10))

        var worker = vertx.createSharedWorkerExecutor("my-test-pool")

        worker.executeBlocking<Any>({ future ->
            println("try blocking execute")
            future.complete()
        }, { result ->
            println(result)
        })

        worker.close()

        vertx.deployVerticle(DemoVertical())

        runAndClose()
    }

    fun close() {
        println("I am closing.")
        vertx.close()
    }

    fun periodicPrint(): Long {
        return vertx.setPeriodic(1000) { id ->
            println("every second")
        }
    }

    fun runAndClose() {
        var worker = vertx.createSharedWorkerExecutor("my-test-pool")

        worker.executeBlocking<Any>({ future ->
            this.periodicPrint()
            future.complete()
        }, {
            println("blocking code set")
        })

        Thread.sleep(10_000)
    }
}

class DemoVertical : AbstractVerticle() {
    override fun start() {
        println("demo vertical start")
        super.start()
    }

    override fun stop() {
        println("demo vertical stop")
        super.stop()
    }
}