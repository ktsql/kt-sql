package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload

class FieldCountPacket: MySQLPacket {
    private val sequenceId: Int
    private val columnCount: Int

    constructor(payload: MySQLPacketPayload) {
        this.sequenceId = payload.readInt1()
        this.columnCount = payload.readInt1()
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return 2
    }

    override fun writeTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeIntLenenc(columnCount.toLong())
        return payload
    }

    fun getColumnCount(): Int {
        return columnCount
    }
}