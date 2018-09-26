package me.principality.ktsql.backend.hbase

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.Enumerable
import org.apache.calcite.schema.ScannableTable
import org.apache.hadoop.hbase.HTableDescriptor

/**
 * 实现对扫描的支持，创建时，初始化相关环境，在scan()时完成对数据的扫描处理
 */
class HBaseScanableTable(name: String, htable: HTableDescriptor) :
        HBaseTable(name, htable), ScannableTable {

    override fun scan(root: DataContext?): Enumerable<Array<Any>> {
        TODO("not implemented")
    }
}