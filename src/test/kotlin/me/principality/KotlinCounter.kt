package me.principality

import com.github.jtendermint.jabci.api.*
import com.github.jtendermint.jabci.socket.TSocket
import com.github.jtendermint.jabci.types.*
import com.google.protobuf.ByteString
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * DApp Demo Application
 */
class KotlinCounter : IDeliverTx, ICheckTx, ICommit, IQuery {

    private val socket = TSocket()
    private var txCount = 0
    private var hashCount = 0
    private var round = 0

    init {
        socket.registerListener(this)
        val thread = Thread {
            socket.start(46658)
        }
        thread.name = "Kotlin Counter Main Thread"
        thread.start()
        while (round < 10) {
            Thread.sleep(2_000)
            println("wake up...")
            round++
        }
    }

    override fun receivedDeliverTx(req: RequestDeliverTx?): ResponseDeliverTx {
        val tx = req!!.getTx()
        println("got deliver tx, with" + TSocket.byteArrayToString(tx.toByteArray()))

        if (tx.size() == 0) {
            println("returning BAD, transaction is empty")
            return ResponseDeliverTx.newBuilder().setCode(CodeType.BadNonce).setLog("transaction is empty").build()
        } else if (tx.size() <= 4) {
            val x = BigInteger(1, tx.toByteArray()).intValueExact()
            // this is an int now, if not throws an ArithmeticException
            // but we dont actually care what it is.

            if (x != txCount) {
                val message = "Invalid Nonce. Expected $txCount, got $x"
                println("returning BAD, $message")
                return ResponseDeliverTx.newBuilder().setCode(CodeType.BadNonce).setLog(message).build()
            }
        } else {
            println("returning BAD, got a bad value")
            return ResponseDeliverTx.newBuilder().setCode(CodeType.BadNonce).setLog("got a bad value").build()
        }

        txCount += 1
        println("TX Count is now: $txCount")
        return ResponseDeliverTx.newBuilder().setCode(CodeType.OK).build()
    }

    override fun requestCheckTx(req: RequestCheckTx?): ResponseCheckTx {
        println("got check tx")

        val tx = req!!.getTx()
        if (tx.size() <= 4) {
            // hopefully parsable integer
            val txCheck = BigInteger(1, tx.toByteArray()).intValueExact()

            println("tx value is: $txCheck")

            if (txCheck < txCount) {
                val err = ("Invalid nonce. Ex"
                        + ""
                        + "pected >= " + txCount + ", got " + txCheck)
                println(err)
                return ResponseCheckTx.newBuilder().setCode(CodeType.BadNonce).setLog(err).build()
            }
        }

        println("SENDING OK")
        return ResponseCheckTx.newBuilder().setCode(CodeType.OK).build()
    }

    override fun requestCommit(requestCommit: RequestCommit?): ResponseCommit {
        hashCount += 1

        if (txCount == 0) {
            return ResponseCommit.newBuilder().build()
        } else {
            val buf = ByteBuffer.allocate(Integer.BYTES)
            buf.putInt(txCount)
            buf.rewind()
            return ResponseCommit.newBuilder().setData(ByteString.copyFrom(buf)).build()
        }
    }

    override fun requestQuery(req: RequestQuery?): ResponseQuery {
        val query = String(req!!.getData().toByteArray(), Charset.forName("UTF-8"))
        when (query) {
            "hash" -> return ResponseQuery.newBuilder().setCode(CodeType.OK)
                    .setValue(ByteString.copyFrom(
                            java.lang.String("" + hashCount).getBytes(Charset.forName("UTF-8")))
                    ).build()
            "tx" -> return ResponseQuery.newBuilder().setCode(CodeType.OK)
                    .setValue(ByteString.copyFrom(
                            java.lang.String("" + txCount).getBytes(Charset.forName("UTF-8")))
                    ).build()
            else -> return ResponseQuery.newBuilder()
                    .setCode(CodeType.BadNonce)
                    .setLog("Invalid query path. Expected hash or tx, got $query")
                    .build()
        }
    }

}