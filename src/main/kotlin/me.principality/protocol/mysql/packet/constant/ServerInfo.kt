package me.principality.protocol.mysql.packet.constant

object ServerInfo {
    /**
     * Protocol version is always 0x0A.
     */
    val PROTOCOL_VERSION = 0x0A

    /**
     * Server version.
     */
    val SERVER_VERSION = "5.6.0-ChainDB 0.1.0.M1"

    /**
     * Charset code 0x21 is utf8_general_ci.
     */
    val CHARSET = 0x21
}