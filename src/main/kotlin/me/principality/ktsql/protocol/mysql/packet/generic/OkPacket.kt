package me.principality.ktsql.protocol.mysql.packet.generic

import com.google.common.base.Preconditions
import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.constant.StatusFlag

class OkPacket: MySQLPacket {
    private val HEADER = 0x00
    private val STATUS_FLAG = StatusFlag.SERVER_STATUS_AUTOCOMMIT.value
    private var sequenceId: Int = 0
    private var affectedRows: Long = 0
    private var lastInsertId: Long = 0
    private var warnings: Int = 0
    private var info: String = ""

    constructor(sequenceId: Int, affectedRows: Long, lastInsertId: Long, warnings: Int, info: String) {
        this.sequenceId = sequenceId // https://stackoverflow.com/questions/50805673/kotlin-operator-assignment
        this.affectedRows = affectedRows
        this.lastInsertId = lastInsertId
        this.warnings = warnings
        this.info = info
    }

    constructor(sequenceId: Int) {
        OkPacket(sequenceId, 0L, 0L, 0, "")
    }

    constructor(sequenceId: Int, affectedRows: Long, lastInsertId: Long) {
        OkPacket(sequenceId, affectedRows, lastInsertId, 0, "")
    }

    constructor(payload: MySQLPacketPayload) {
        this.sequenceId = payload.readInt1()
        Preconditions.checkArgument(HEADER == payload.readInt1())
        affectedRows = payload.readIntLenenc()
        lastInsertId = payload.readIntLenenc()
        payload.readInt2()
        warnings = payload.readInt2()
        info = payload.readStringEOF()
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return (1
                + intLenenc(affectedRows)
                + intLenenc(lastInsertId)
                + 2
                + 2
                + info.length
                )
    }

    override fun transferTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeInt3(getPacketSize())
        payload.writeInt1(payload.id)
        payload.writeInt1(HEADER)
        payload.writeIntLenenc(affectedRows)
        payload.writeIntLenenc(lastInsertId)
        payload.writeInt2(STATUS_FLAG)
        payload.writeInt2(warnings)
        payload.writeStringEOF(info)

        assert (payload.byteBuffer.length() == payload.size + 4)
        return payload
    }
}