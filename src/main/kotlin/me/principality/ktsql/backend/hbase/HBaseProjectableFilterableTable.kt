package me.principality.ktsql.backend.hbase

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.Enumerable
import org.apache.calcite.rex.RexNode
import org.apache.calcite.schema.ProjectableFilterableTable
import org.apache.hadoop.hbase.HTableDescriptor

class HBaseProjectableFilterableTable(name: String, htable: HTableDescriptor) :
        HBaseTable(name, htable), ProjectableFilterableTable {

    override fun scan(root: DataContext?,
                      filters: MutableList<RexNode>?,
                      projects: IntArray?): Enumerable<Array<Any>> {
        TODO("not implemented")
    }
}