package me.principality.ktsql.backend.hbase

import me.principality.ktsql.sqlexec.SqlPacketHandler
import me.principality.ktsql.sqlexec.SqlUtil
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.*
import org.apache.hadoop.hbase.util.Bytes
import org.junit.Test
import java.nio.charset.Charset

class HBaseTest {

    @Test
    fun testValueFilter() {
        SqlUtil.init()
        val handler = SqlPacketHandler()

        val htable = HBaseConnection.connection().getTable(TableName.valueOf("T"))
        val scan = Scan()
        val filterList = FilterList(FilterList.Operator.MUST_PASS_ALL)

        // 测试扫描的过滤条件
        val f = SingleColumnValueFilter(Bytes.toBytes(HBaseTable.columnFamily),//列族
                Bytes.toBytes("POS"),  //列名
                CompareFilter.CompareOp.GREATER, Bytes.toBytes(0))
        filterList.addFilter(f)
        scan.setFilter(filterList)
        val rs = htable.getScanner(scan)
        for (result in rs) {
            println("${result.row.toString(Charset.defaultCharset())}")
        }
        println(rs)
    }

    @Test
    fun testRowFilter() {
        SqlUtil.init()
        val handler = SqlPacketHandler()

        val htable = HBaseConnection.connection().getTable(TableName.valueOf("T"))
        val scan = Scan()
        val filterList = FilterList(FilterList.Operator.MUST_PASS_ALL)

        // 测试扫描的过滤条件
        val f = RowFilter(CompareFilter.CompareOp.EQUAL, BinaryComparator(Bytes.toBytes("XXXX")))
        filterList.addFilter(f)
        scan.setFilter(filterList)
        val rs = htable.getScanner(scan)
        for (result in rs) {
            println("${result.row.toString(Charset.defaultCharset())}")
        }
        println(rs)
    }
}