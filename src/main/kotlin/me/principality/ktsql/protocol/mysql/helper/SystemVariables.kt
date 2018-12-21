package me.principality.ktsql.protocol.mysql.helper

/**
 * 模拟并返回客户端感兴趣的MySQL系统参数
 *
 * http://www.cnblogs.com/maobuji/p/9143794.html
 * https://dev.mysql.com/doc/refman/5.7/en/server-system-variables.html
 */
object SystemVariables {
    val keyvalueMap = HashMap<String, Any>()

    init {
        keyvalueMap.put("@@session.auto_increment_increment", 1)
        keyvalueMap.put("@@character_set_client", "utf8")
        keyvalueMap.put("@@character_set_connection", "utf8")
        keyvalueMap.put("@@character_set_results", "utf8")
        keyvalueMap.put("@@character_set_server", "utf8")
        keyvalueMap.put("@@collation_server", "utf8_general_ci") // utf8_general_ci ??
        keyvalueMap.put("@@collation_connection", "utf8_general_ci")
        keyvalueMap.put("@@init_connect", "")
        keyvalueMap.put("@@interactive_timeout", 28800)
        keyvalueMap.put("@@license", "")
        keyvalueMap.put("@@lower_case_table_names", 2)
        keyvalueMap.put("@@max_allowed_packet", 4194304) // todo
        keyvalueMap.put("@@net_buffer_length", 16384)
        keyvalueMap.put("@@net_write_timeout", 60)
        keyvalueMap.put("@@query_cache_size", 1048576)
        keyvalueMap.put("@@query_cache_type", "OFF")
        keyvalueMap.put("@@sql_mode", "ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION")
        keyvalueMap.put("@@system_time_zone", "CST")
        keyvalueMap.put("@@time_zone", "SYSTEM")
        keyvalueMap.put("@@tx_isolation", "REPEATABLE-READ")
        keyvalueMap.put("@@wait_timeout", 28800)
    }

    fun getValue(key: String): String {
        return keyvalueMap.get(key).toString()
    }
}