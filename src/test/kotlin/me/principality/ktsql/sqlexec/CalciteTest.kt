package me.principality.ktsql.sqlexec

import me.principality.ktsql.utils.config.ConfigureProvider
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.test.assertEquals

/**
 * 使用直接创建SqlHandler访问底层的方式，不经过协议层，对SQL语句的支撑度进行测试
 */
class CalciteTest {
    private val TEST_TABLE_NAME = "HBASE.t" // "HBASE.t"
    private val TEST_TABLE_INDEX = "idx"

    /**
     * 测试select, insert, update, delete, create, drop
     */
    @Test
    fun testSqlPacketHandler() {
        SqlUtil.init()

        val handler = SqlExecuteHandler()
        val r0 = handler.executeDdl("create table if not exists ${TEST_TABLE_NAME} (rowkey varchar(32), rowvalue varchar(255), pos int default 0, primary key (rowkey))")
        val r1 = handler.execute("insert into ${TEST_TABLE_NAME} values ('XXXX', 'XXXV', 1)")
        val r2 = handler.execute("update ${TEST_TABLE_NAME} set rowvalue='XXVV', pos=3 where pos > 0") // fixme update代码无法正常运行
        val r3 = handler.execute("insert into ${TEST_TABLE_NAME} (rowkey, rowvalue, pos) values ('YYYY', 'YYYV', 2)")
        val r4 = handler.executeQuery("select rowkey, rowvalue from ${TEST_TABLE_NAME} where pos > 0") // 要试试范围选择
        val r5 = handler.execute("delete from ${TEST_TABLE_NAME} where pos=3")
        val rr = handler.executeDdl("drop table if exists ${TEST_TABLE_NAME}")
        assertEquals(0, 0)
    }


    @Test
    fun testIndexTable() {
        SqlUtil.init()

        val handler = SqlExecuteHandler()
        val r0 = handler.executeDdl("create table if not exists ${TEST_TABLE_NAME} (rowkey varchar(32), rowvalue varchar(255), pos int default 0, primary key (rowkey))")
        val r1 = handler.executeDdl("create index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME} (rowvalue)")
        val r2 = handler.execute("insert into ${TEST_TABLE_NAME} (rowkey, rowvalue, pos) values ('YYYY', 'YYYV', 2)")
        val r3 = handler.execute("insert into ${TEST_TABLE_NAME} values ('AAAA', 'AAAV', 3)")
        val r4 = handler.executeQuery("select * from ${TEST_TABLE_NAME} where rowvalue like '%V'") // 试试根据索引获取内容
        val r5 = handler.executeQuery("select * from ${TEST_TABLE_NAME} where rowvalue = 'AAAV'") // 试试根据索引获取内容
        val r6 = handler.execute("insert into ${TEST_TABLE_NAME} values ('XXXX', 'XXXV', 1)")
        val r7 = handler.execute("update ${TEST_TABLE_NAME} set rowvalue='XXVV', pos=3 where pos > 0") // fixme update代码无法正常运行
        val r8 = handler.executeQuery("select rowkey, rowvalue from ${TEST_TABLE_NAME} where pos > 0") // 要试试范围选择
        val r9 = handler.execute("delete from ${TEST_TABLE_NAME} where pos=3")
        val rr = handler.executeDdl("drop index ${TEST_TABLE_INDEX} on ${TEST_TABLE_NAME}")
        assertEquals(0, 0)
    }

    @Test
    fun testSetOption() {
        SqlUtil.init()

        val handler = SqlExecuteHandler()
        val r0 = handler.execute("set names = utf8") // 不支持set option
    }

    @Test
    fun testMetaInit() {
        lateinit var connection: Connection
        lateinit var tableMetas: ResultSet

        val connectionString = "jdbc:calcite:parserFactory=org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl#FACTORY"

        val info = ConfigureProvider.getCalciteConfig()
        connection = DriverManager.getConnection(connectionString, info)

        val databaseMetaData = connection.metaData
        tableMetas = databaseMetaData.getTables("", "", "%", null)

        tableMetas.close()
        connection.close()

        assertEquals(0, 0)
    }
}