package me.principality.ktsql.protocol.mysql.packet.handshake

import me.principality.ktsql.protocol.mysql.packet.MySQLPacket
import me.principality.ktsql.protocol.mysql.packet.MySQLPacketPayload
import me.principality.ktsql.protocol.mysql.packet.constant.CapabilityFlag

class HandshakeResponse41Packet: MySQLPacket {
    private val sequenceId: Int
    private val capabilityFlags: Int
    private val maxPacketSize: Int
    private val characterSet: Int
    val username: String
    var authResponse: ByteArray? = null
        private set
    var database: String? = null
        private set

    constructor(payload: MySQLPacketPayload) {
        val size = payload.readInt3()
        sequenceId = payload.readInt1()
        capabilityFlags = payload.readInt4()
        maxPacketSize = payload.readInt4()
        characterSet = payload.readInt1()
        payload.skipReserved(23)
        username = payload.readStringNul()
        readAuthResponse(payload)
        readDatabase(payload)
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return (4
                + 4
                + 1
                + 23
                + username.length
                )
    }

    override fun transferTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeInt4(capabilityFlags)
        payload.writeInt4(maxPacketSize)
        payload.writeInt1(characterSet)
        payload.writeReserved(23)
        payload.writeStringNul(username)
        writeAuthResponse(payload)
        writeDatabase(payload)
        return payload
    }

    private fun readAuthResponse(payload: MySQLPacketPayload) {
        if (0 != capabilityFlags and CapabilityFlag.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.value) {
            authResponse = payload.readStringLenencByBytes()
        } else if (0 != capabilityFlags and CapabilityFlag.CLIENT_SECURE_CONNECTION.value) {
            val length = payload.readInt1()
            authResponse = payload.readStringFixByBytes(length)
        } else {
            authResponse = payload.readStringNulByBytes()
        }
    }

    private fun readDatabase(payload: MySQLPacketPayload) {
        if (0 != capabilityFlags and CapabilityFlag.CLIENT_CONNECT_WITH_DB.value) {
            database = payload.readStringNul()
        }
    }

    private fun writeAuthResponse(payload: MySQLPacketPayload) {
        if (0 != capabilityFlags and CapabilityFlag.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.value) {
            payload.writeStringLenenc(String(authResponse!!))
        } else if (0 != capabilityFlags and CapabilityFlag.CLIENT_SECURE_CONNECTION.value) {
            payload.writeInt1(authResponse!!.size)
            payload.writeBytes(authResponse!!)
        } else {
            payload.writeStringNul(String(authResponse!!))
        }
    }

    private fun writeDatabase(payload: MySQLPacketPayload) {
        if (0 != capabilityFlags and CapabilityFlag.CLIENT_CONNECT_WITH_DB.value) {
            payload.writeStringNul(database!!)
        }
    }
}