package me.principality.ktsql.protocol.mysql.packet.generic

import com.google.common.base.Preconditions
import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.constant.StatusFlag

class EofPacket: MySQLPacket {
    private val HEADER = 0xfe
    private var sequenceId: Int = 0
    private var warnings: Int = 0
    private var statusFlags: Int = 0

    constructor(sequenceId: Int, warnings: Int, statusFlags: Int) {
        this.sequenceId = sequenceId
        this.warnings = warnings
        this.statusFlags = statusFlags
    }
    constructor(sequenceId: Int) {
        EofPacket(sequenceId, 0, StatusFlag.SERVER_STATUS_AUTOCOMMIT.value)
    }

    constructor(payload: MySQLPacketPayload) {
        sequenceId = payload.readInt1()
        Preconditions.checkArgument(HEADER == payload.readInt1())
        warnings = payload.readInt2()
        statusFlags = payload.readInt2()
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return (1
                + 2
                + 2
                )
    }

    override fun writeTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeInt1(HEADER)
        payload.writeInt2(warnings)
        payload.writeInt2(statusFlags)
        return payload
    }
}