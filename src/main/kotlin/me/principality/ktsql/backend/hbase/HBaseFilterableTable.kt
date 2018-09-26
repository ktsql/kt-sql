package me.principality.ktsql.backend.hbase

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.Enumerable
import org.apache.calcite.rex.RexNode
import org.apache.calcite.schema.FilterableTable
import org.apache.hadoop.hbase.HTableDescriptor

class HBaseFilterableTable(name: String, htable: HTableDescriptor) :
        HBaseTable(name, htable), FilterableTable {

    override fun scan(root: DataContext?,
                      filters: MutableList<RexNode>?): Enumerable<Array<Any>> {
        TODO("not implemented")
    }
}