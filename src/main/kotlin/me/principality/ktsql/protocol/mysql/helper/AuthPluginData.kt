package me.principality.ktsql.protocol.mysql.helper

import com.google.common.primitives.Bytes

class AuthPluginData {
    val authPluginDataPart1: ByteArray
    val authPluginDataPart2: ByteArray

    constructor() {
        this.authPluginDataPart1 = RandomGenerator.generateRandomBytes(8)
        this.authPluginDataPart2 = RandomGenerator.generateRandomBytes(12)

        for (i in 0..7) {
            this.authPluginDataPart1.set(i, 'X'.toByte())
        }

        for (i in 0..11) {
            this.authPluginDataPart2.set(i, 'Y'.toByte())
        }
    }

    constructor(authPluginDataPart1: ByteArray, authPluginDataPart2: ByteArray) {
        this.authPluginDataPart1 = authPluginDataPart1
        this.authPluginDataPart2 = authPluginDataPart2
    }

    /**
     * Get auth plugin data.
     *
     * @return auth plugin data
     */
    fun getAuthPluginData(): ByteArray {
        return Bytes.concat(authPluginDataPart1, authPluginDataPart2)
    }
}