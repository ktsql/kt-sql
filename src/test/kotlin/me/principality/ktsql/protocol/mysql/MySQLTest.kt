package me.principality.ktsql.protocol.mysql

import org.junit.Test
import java.sql.DriverManager

class MySQLTest {
    @Test
    fun testProtocol() {
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:30000/hbase","user","pass")
        val statement = conn.createStatement()
        val result = statement.executeQuery("select * from user")
        while (result.next()) {
            println(result.getString(""))
        }
        statement.close()
        conn.close()
    }
}