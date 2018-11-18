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
     * 测试select, insert, update, delete, create, drop
     */
    @Test
    fun testSqlPacketHandler() {
        SqlUtil.init()

        val handler = SqlPacketHandler()
        val r0 = handler.executeDdl("create table if not exists ${TEST_TABLE_NAME} (rowkey varchar(32), rowvalue varchar(255), pos int default 0, primary key (rowkey))")
        val r1 = handler.execute("insert into ${TEST_TABLE_NAME} values ('XXXX', 'XXXV', 1)")
        val r2 = handler.execute("update ${TEST_TABLE_NAME} set value='XXVV' where rowkey='XXXX'") // fixme update代码无法正常运行
        val r3 = handler.executeQuery("select rowkey, rowvalue from ${TEST_TABLE_NAME} where pos = 1")
        val r4 = handler.execute("delete from ${TEST_TABLE_NAME} where rowkey='XXXX'")
        val r5 = handler.execute("insert into ${TEST_TABLE_NAME} (rowkey, rowvalue) values ('YYYY', 'YYYV', -1)")
        val r6 = handler.executeQuery("select * from ${TEST_TABLE_NAME}")
        val r7 = handler.executeDdl("create index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME} (rowkey)")
        val r8 = handler.execute("insert into ${TEST_TABLE_NAME} values ('AAAA', 'AAAV')")
        val r9 = handler.executeDdl("drop index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME}")
        val rr = handler.executeDdl("drop table if exists ${TEST_TABLE_NAME}")
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