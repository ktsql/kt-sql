package me.principality.ktsql.protocol.mysql.helper

import java.sql.ResultSet

/**
 * 定义命令实现的接口。每一个实现了该接口的工具类，都可用于命令的实际执行。
 * 这里使用jdbc的定义，作为从底层获取数据的接口，简化设计的复杂度
 */
interface PacketHandleHelper : AutoCloseable {
    fun executeQuery(sql: String): ResultSet
    fun executeDdl(sql: String): Int
    fun execute(sql: String): Boolean
}