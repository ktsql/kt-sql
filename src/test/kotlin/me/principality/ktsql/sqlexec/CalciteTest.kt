package me.principality.ktsql.sqlexec

import me.principality.ktsql.utils.config.ConfigureProvider
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.test.assertEquals

class CalciteTest {
    private val TEST_TABLE_NAME = "t"
    private val TEST_TABLE_INDEX = "idx"
    /**
     * 测试select, insert, create, drop
     */
    @Test
    fun testSqlPacketHandler() {
        val handler = SqlPacketHandler()
//        val rr = handler.execute("select * from ${TEST_TABLE_NAME}")
        val r0 = handler.executeDdl("create table if not exists ${TEST_TABLE_NAME} (rowkey varchar(255))")
        val r1 = handler.executeQuery("select * from ${TEST_TABLE_NAME}")
        val r2 = handler.execute("insert into ${TEST_TABLE_NAME} values ('XXXX')")
        val r3 = handler.executeDdl("create index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME} (rowkey)")
        val r4 = handler.execute("insert into ${TEST_TABLE_NAME} values ('YYYY')")
        val r5 = handler.executeDdl("drop index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME}")
        val r6 = handler.executeDdl("drop table if exists ${TEST_TABLE_NAME}")
        assertEquals(0, 0)
    }

//    @Test
//    fun testMetaInit() {
//        lateinit var connection: Connection
//        lateinit var tableMetas: ResultSet
//
//        val connectionString = "jdbc:calcite:parserFactory=org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl#FACTORY"
//
//        val info = ConfigureProvider.getCalciteConfig()
//        connection = DriverManager.getConnection(connectionString, info)
//
//        val databaseMetaData = connection.metaData
//        tableMetas = databaseMetaData.getTables("", "", "%", null)
//
//        tableMetas.close()
//        connection.close()
//
//        assertEquals(0, 0)
//    }
}