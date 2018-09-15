package me.principality.protocol.mysql.packet.constant

enum class ServerErrorCode(val err: Int, val sqlstate: String, val reason: String) {
    ER_ACCESS_DENIED_ERROR(1045, "28000", "Access denied for user '%s'@'%s' (using password: %s)"),
    ER_BAD_DB_ERROR(1049, "42000", "Unknown database '%s'"),
    ER_ERROR_ON_MODIFYING_GTID_EXECUTED_TABLE(3176, "HY000",
            "Please do not modify the %s table with an XA transaction. "
                    + "This is an internal system table used to store GTIDs for committed transactions. "
                    + "Although modifying it can lead to an inconsistent GTID state, if neccessary you can modify it with a non-XA transaction."),
    ER_STD_UNKNOWN_EXCEPTION(3054, "HY000", "Unknown exception: %s"),
    ER_UNSUPPORTED_COMMAND(9999, "XXXXX", "Unsupported command packet: '%s'");
}