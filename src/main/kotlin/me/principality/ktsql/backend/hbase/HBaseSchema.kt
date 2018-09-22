package me.principality.ktsql.backend.hbase

import org.apache.calcite.schema.impl.AbstractSchema

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
 * 以下是asynchbase中的相关说明：
 *
 * 摘录自HBaseClient的注释
 *
 * 1. HBaseClient每连接只需创建一次（共享给多个协程使用）
 * 2. 通过HBaseRpc请求HBaseClient，未完成回调前，不可修改HBaseRpc
 * 3. Exception采用对defer进行处理的模式
 *
 * 摘录自RegionClient的注释
 * 1. RegionClient并非线程安全
 *
 * 摘录自ZKClient的注释
 * 1. zookeeper提供region server是否在线以及RootRegion的信息
 */
class HBaseSchema: AbstractSchema {
    constructor() {
    }
}