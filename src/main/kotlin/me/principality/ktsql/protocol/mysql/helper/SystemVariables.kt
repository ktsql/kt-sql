package me.principality.ktsql.protocol.mysql.helper

import me.principality.ktsql.protocol.mysql.packet.constant.ColumnType

/**
 * 模拟并返回客户端感兴趣的MySQL系统参数
 *
 * http://www.cnblogs.com/maobuji/p/9143794.html
 * https://dev.mysql.com/doc/refman/5.7/en/server-system-variables.html
 */
object SystemVariables {
    val keyValueMap = HashMap<String, Any>()
    val keyLengthMap = HashMap<String, Int>()
    val keyTypeMap = HashMap<String, ColumnType>()

    init {
        keyValueMap.put("@@session.auto_increment_increment", 1)
        keyValueMap.put("@@character_set_client", "utf8")
        keyValueMap.put("@@character_set_connection", "utf8")
        keyValueMap.put("@@character_set_results", "utf8")
        keyValueMap.put("@@character_set_server", "utf8")
        keyValueMap.put("@@collation_server", "utf8_general_ci") // utf8_general_ci ??
        keyValueMap.put("@@collation_connection", "utf8_general_ci")
        keyValueMap.put("@@init_connect", "")
        keyValueMap.put("@@interactive_timeout", 28800)
        keyValueMap.put("@@license", "")
        keyValueMap.put("@@lower_case_table_names", 2)
        keyValueMap.put("@@max_allowed_packet", 4194304) // todo
        keyValueMap.put("@@net_buffer_length", 16384)
        keyValueMap.put("@@net_write_timeout", 60)
        keyValueMap.put("@@query_cache_size", 1048576)
        keyValueMap.put("@@query_cache_type", "OFF")
        keyValueMap.put("@@sql_mode", "ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION")
        keyValueMap.put("@@system_time_zone", "CST")
        keyValueMap.put("@@time_zone", "SYSTEM")
        keyValueMap.put("@@tx_isolation", "REPEATABLE-READ")
        keyValueMap.put("@@wait_timeout", 28800)

        keyLengthMap.put("@@session.auto_increment_increment", 8)
        keyLengthMap.put("@@character_set_client", "utf8".length)
        keyLengthMap.put("@@character_set_connection", "utf8".length)
        keyLengthMap.put("@@character_set_results", "utf8".length)
        keyLengthMap.put("@@character_set_server", "utf8".length)
        keyLengthMap.put("@@collation_server", "utf8_general_ci".length)
        keyLengthMap.put("@@collation_connection", "utf8_general_ci".length)
        keyLengthMap.put("@@init_connect", "".length)
        keyLengthMap.put("@@interactive_timeout", 8)
        keyLengthMap.put("@@license", "".length)
        keyLengthMap.put("@@lower_case_table_names", 8)
        keyLengthMap.put("@@max_allowed_packet", 8)
        keyLengthMap.put("@@net_buffer_length", 8)
        keyLengthMap.put("@@net_write_timeout", 8)
        keyLengthMap.put("@@query_cache_size", 8)
        keyLengthMap.put("@@query_cache_type", "OFF".length)
        keyLengthMap.put("@@sql_mode", "ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION".length)
        keyLengthMap.put("@@system_time_zone", "CST".length)
        keyLengthMap.put("@@time_zone", "SYSTEM".length)
        keyLengthMap.put("@@tx_isolation", "REPEATABLE-READ".length)
        keyLengthMap.put("@@wait_timeout", 8)

        keyTypeMap.put("@@session.auto_increment_increment", ColumnType.MYSQL_TYPE_LONG)
        keyTypeMap.put("@@character_set_client", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@character_set_connection", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@character_set_results", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@character_set_server", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@collation_server", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@collation_connection", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@init_connect", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@interactive_timeout", ColumnType.MYSQL_TYPE_LONG)
        keyTypeMap.put("@@license", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@lower_case_table_names", ColumnType.MYSQL_TYPE_LONG)
        keyTypeMap.put("@@max_allowed_packet", ColumnType.MYSQL_TYPE_LONG)
        keyTypeMap.put("@@net_buffer_length", ColumnType.MYSQL_TYPE_LONG)
        keyTypeMap.put("@@net_write_timeout", ColumnType.MYSQL_TYPE_LONG)
        keyTypeMap.put("@@query_cache_size", ColumnType.MYSQL_TYPE_LONG)
        keyTypeMap.put("@@query_cache_type", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@sql_mode", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@system_time_zone", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@time_zone", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@tx_isolation", ColumnType.MYSQL_TYPE_STRING)
        keyTypeMap.put("@@wait_timeout", ColumnType.MYSQL_TYPE_LONG)
    }

    fun getValue(key: String): Any? {
        return keyValueMap.get(key)
    }

    fun getLength(key: String): Int? {
        return keyLengthMap.get(key)
    }

    fun getType(key: String): ColumnType? {
        return keyTypeMap.get(key)
    }
}