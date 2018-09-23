package me.principality.ktsql.backend.hbase

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.Enumerable
import org.apache.calcite.schema.ScannableTable

/**
 * 实现对扫描的支持，创建时，初始化相关环境，在scan()时通过调用asysnchbase
 * 完成对数据的扫描处理
 */
class HBaseScanableTable: HBaseTable, ScannableTable{

    constructor() {

    }

    override fun scan(root: DataContext?): Enumerable<Array<Any>> {
        TODO("not implemented")
    }
}