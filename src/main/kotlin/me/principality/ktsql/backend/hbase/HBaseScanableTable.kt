package me.principality.ktsql.backend.hbase

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.Enumerable
import org.apache.calcite.schema.ScannableTable

/**
 * 实现对扫描的支持
 */
class HBaseScanableTable: HBaseTable, ScannableTable{

    constructor() {

    }

    override fun scan(root: DataContext?): Enumerable<Array<Any>> {
        TODO("not implemented")
    }
}