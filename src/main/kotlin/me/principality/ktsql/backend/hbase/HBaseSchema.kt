package me.principality.ktsql.backend.hbase

import com.google.common.collect.ImmutableMap
import mu.KotlinLogging
import org.apache.calcite.schema.Table
import org.apache.calcite.schema.impl.AbstractSchema
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.Admin

/**
 * Calcite对表的创建有几种：
 *
 * 1. 根据配置，初始化生成不同类型的表，
 * 2. 使用Schema对没有配置的Table进行创建
 *
 * 在HBase里面，没有database的概念，所有的表都在同一个级别上
 * 使用HBaseAdmin对应Schema
 *
 * HBaseSchema的核心函数是getTable，实例需维护 Map<String, Table>
 *
 * 根据HBase的架构，要读取数据时，首先需去RootRegion获取RowKey对应的MetaRegion，
 * 再从MetaRegion获得最终数据所在的RegionServer，此时再使用RegionClient读取数据
 *
 * 从jdbc调用后端的过程为：
 * 1. 创建connection，此时会完成schema的创建，schema需要准备好表创建的逻辑
 * 2. 通过connection获取statement，并通过statement执行sql，此时会创建表
 *
 * TODO calcite没有释放connection, admin, htable的机制
 */
class HBaseSchema : AbstractSchema {
    private val logger = KotlinLogging.logger {}
    private val connection: Connection
    private lateinit var tableMap: Map<String, Table>

    constructor(connection: Connection) {
        this.connection = connection
    }

    override fun getTableMap(): Map<String, Table> {
        if (tableMap == null) {
            tableMap = createTableMap()
        }
        return tableMap
    }

    private fun createTableMap(): Map<String, Table> {
        val admin = connection.admin
        val tables = admin.listTables()

        if (tables.size == 0) {
            logger.debug("find none table")
        }

        val builder = ImmutableMap.builder<String, Table>()
        for (table in tables) {
            val source = table.nameAsString
            val table = createTable(source, table)
            builder.put(source, table)
        }
        tableMap = builder.build()
        return tableMap
    }

    private fun createTable(name: String, table: HTableDescriptor): Table {
        when (HBaseConnection.flavor()) {
            HBaseTable.Flavor.SCANNABLE -> return HBaseScanableTable(name, table)
            HBaseTable.Flavor.FILTERABLE -> return HBaseFilterableTable(name, table)
            HBaseTable.Flavor.PROJECTFILTERABLE -> return HBaseProjectableFilterableTable(name, table)
            else -> throw AssertionError("Unknown flavor " + HBaseConnection.flavor())
        }
    }
}