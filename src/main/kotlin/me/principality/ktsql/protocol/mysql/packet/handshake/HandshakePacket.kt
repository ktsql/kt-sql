package me.principality.ktsql.protocol.mysql.packet.handshake

import com.google.common.base.Preconditions
import me.principality.ktsql.protocol.mysql.helper.AuthPluginData
import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.constant.CapabilityFlag
import me.principality.ktsql.protocol.mysql.packet.constant.ServerInfo
import me.principality.ktsql.protocol.mysql.packet.constant.StatusFlag

class HandshakePacket : MySQLPacket {
    private val protocolVersion = ServerInfo.PROTOCOL_VERSION
    private val serverVersion = ServerInfo.SERVER_VERSION
    private val capabilityFlagsLower = CapabilityFlag.calculateHandshakeCapabilityFlagsLower()
    private val characterSet = ServerInfo.CHARSET
    private val statusFlag = StatusFlag.SERVER_STATUS_AUTOCOMMIT
    private val capabilityFlagsUpper = CapabilityFlag.calculateHandshakeCapabilityFlagsUpper()

    private val sequenceId: Int
    private val connectionId: Int
    private val authPluginData: AuthPluginData

    constructor(connectionId: Int, authPluginData: AuthPluginData) {
        sequenceId = 0
        this.connectionId = connectionId
        this.authPluginData = authPluginData
    }

    constructor(payload: MySQLPacketPayload) {
        sequenceId = payload.readInt1()
        Preconditions.checkArgument(protocolVersion == payload.readInt1())
        payload.readStringNul()
        connectionId = payload.readInt4()
        val authPluginDataPart1 = payload.readStringNul().toByteArray()
        payload.readInt2()
        payload.readInt1()
        Preconditions.checkArgument(statusFlag.value === payload.readInt2())
        payload.readInt2()
        payload.readInt1()
        payload.skipReserved(10)
        val authPluginDataPart2 = payload.readStringNul().toByteArray()
        authPluginData = AuthPluginData(authPluginDataPart1, authPluginDataPart2)
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return (1
                + serverVersion.length
                + 4
                + authPluginData.authPluginDataPart1.size
                + 2
                + 1
                + 2
                + 2
                + 1
                + 10
                + authPluginData.authPluginDataPart2.size
                )
    }

    override fun writeTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeInt3(payload.size)
        payload.writeInt1(payload.id)
        payload.writeInt1(protocolVersion)
        payload.writeStringNul(serverVersion)
        payload.writeInt4(connectionId)
        payload.writeStringNul(String(authPluginData.authPluginDataPart1))
        payload.writeInt2(capabilityFlagsLower)
        payload.writeInt1(ServerInfo.CHARSET)
        payload.writeInt2(statusFlag.value)
        payload.writeInt2(capabilityFlagsUpper)
        payload.writeInt1(0)
        payload.writeReserved(10)
        payload.writeStringNul(String(authPluginData.authPluginDataPart2))

        return payload
    }
}