package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload

class ColumnCountPacket: MySQLPacket {
    private val sequenceId: Int
    private val columnCount: Int

    constructor(id: Int, count: Int) {
        sequenceId = id
        columnCount = count
    }

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

    override fun transferTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeIntLenenc(columnCount.toLong())
        return payload
    }

    fun getColumnCount(): Int {
        return columnCount
    }
}