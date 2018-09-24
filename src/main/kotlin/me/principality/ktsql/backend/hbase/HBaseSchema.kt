package me.principality.ktsql.backend.hbase

import org.apache.calcite.schema.impl.AbstractSchema
import org.apache.hadoop.hbase.client.Connection

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
 * 考虑到HBase会动态扩展，所以每次表连接时获取region list才能保证拿到，
 * 有两种方法可以保证这一点：1、监听zookeeper的变化；2、如果region client报错，重新连接
 *
 * 为了保证性能，现采用后者处理region list
 * 1、schema获取region list并缓存
 * 2、schema提供重新获取region list的接口，并初始化到table中
 * 3、如果table发现regionclient出错，通过schema重新获取region list
 */
class HBaseSchema: AbstractSchema {
    val connection: Connection

    constructor(connection: Connection) {
        this.connection = connection
    }
}