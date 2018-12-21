package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import java.util.*

class TextResultSetRowPacket : MySQLPacket {
    private val NULL = 0xfb
    private var sequenceId: Int
    private var data: MutableList<Any>

    constructor(payload: MySQLPacketPayload, columnCount: Int) {
        sequenceId = payload.readInt1()
        data = ArrayList(columnCount)
        for (i in 0 until columnCount) {
            data.add(payload.readStringLenenc())
        }
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        TODO("not implemented")
    }

    override fun transferTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        for (each in data) {
            if (null == each) {
                payload.writeInt1(NULL)
            } else {
                if (each is ByteArray) {
                    payload.writeBytesLenenc(each)
                } else {
                    payload.writeStringLenenc(each.toString())
                }
            }
        }
        return payload
    }
}