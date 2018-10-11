package me.principality.ktsql.sqlexec

import me.principality.ktsql.utils.config.ConfigureManager
import org.junit.Test
import java.sql.DriverManager
import kotlin.test.assertEquals

class CalciteTest {
//    @Test
//    fun testSqlPacketHandler() {
//        val handler = SqlPacketHandler()
//        val r = handler.execute("select * from table")
//        assertEquals(0, 0)
//    }

    @Test
    fun testMetaFunc() {
        val connectionString = "jdbc:calcite:parserFactory=org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl#FACTORY"

        val info = ConfigureManager.getCalciteConfig()
        val connection = DriverManager.getConnection(connectionString, info)

        val metaData = connection.metaData
        val schema = connection.schema
        val catalog = connection.catalog

        connection.close()
    }
}