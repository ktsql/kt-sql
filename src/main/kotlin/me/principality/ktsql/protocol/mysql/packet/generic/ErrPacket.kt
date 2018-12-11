package me.principality.ktsql.protocol.mysql.packet.generic

import com.google.common.base.Preconditions
import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.constant.ServerErrorCode
import java.sql.SQLException

class ErrPacket: MySQLPacket {
    private val HEADER = 0xff
    private val SQL_STATE_MARKER = "#"
    private var sequenceId: Int = 0
    private var errorCode: Int = 0
    private var sqlState: String = ""
    private var errorMessage: String = ""

    constructor(sequenceId: Int, serverErrorCode: Int, sqlState: String, errorMessage: String) {
        this.sequenceId = sequenceId
        this.errorCode = serverErrorCode
        this.sqlState = sqlState
        this.errorMessage = errorMessage
    }

    constructor(sequenceId: Int, serverErrorCode: ServerErrorCode, message: String) {
        ErrPacket(sequenceId, serverErrorCode.err, serverErrorCode.sqlstate, message)
    }

    constructor(sequenceId: Int, cause: SQLException) {
        ErrPacket(sequenceId, cause.errorCode, cause.sqlState, cause.message
                ?: "")
    }

    constructor(payload: MySQLPacketPayload) {
        sequenceId = payload.readInt1()
        Preconditions.checkArgument(HEADER == payload.readInt1())
        errorCode = payload.readInt2()
        Preconditions.checkArgument(SQL_STATE_MARKER == payload.readStringFix(1))
        sqlState = payload.readStringFix(5)
        errorMessage = payload.readStringEOF()
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return (1
                + 2
                + SQL_STATE_MARKER.length
                + sqlState.length
                + errorMessage.length
                )
    }

    override fun writeTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeInt3(getPacketSize())
        payload.writeInt1(sequenceId)
        payload.writeInt1(HEADER)
        payload.writeInt2(errorCode)
        payload.writeStringFix(SQL_STATE_MARKER)
        payload.writeStringFix(sqlState)
        payload.writeStringEOF(errorMessage)
        return payload
    }
}