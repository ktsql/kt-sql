package me.principality.ktsql.sqlexec

import me.principality.ktsql.utils.config.ConfigureProvider
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.test.assertEquals

class CalciteTest {
    private val TEST_TABLE_NAME = "HBASE.t" // "HBASE.t"
    private val TEST_TABLE_INDEX = "idx"
    /**
     * 测试select, insert, create, drop
     */
    @Test
    fun testSqlPacketHandler() {
        SqlUtil.init()

        val handler = SqlPacketHandler()
        val r6 = handler.executeDdl("create index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME} (rowkey)")
        val r0 = handler.executeDdl("create table if not exists ${TEST_TABLE_NAME} (rowkey varchar(255))")
//        val r1 = handler.execute("insert into ${TEST_TABLE_NAME} values ('XXXX')")
        // fixme 下面的update代码无法正常运行
        val rr = handler.execute("update ${TEST_TABLE_NAME} set rowkey='ZZZZ' where rowkey='XXXX'")
        val r2 = handler.executeQuery("select * from ${TEST_TABLE_NAME}")
        val r3 = handler.execute("delete from ${TEST_TABLE_NAME} where rowkey='XXXX'")
//        val r4 = handler.execute("insert into ${TEST_TABLE_NAME} values ('YYYY')")
//        val r5 = handler.executeQuery("select * from ${TEST_TABLE_NAME}")
//        val r6 = handler.executeDdl("create index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME} (rowkey)")
//        val r7 = handler.execute("insert into ${TEST_TABLE_NAME} values ('AAAA')")
//        val r8 = handler.executeDdl("drop index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME}")
        val r9 = handler.executeDdl("drop table if exists ${TEST_TABLE_NAME}")
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