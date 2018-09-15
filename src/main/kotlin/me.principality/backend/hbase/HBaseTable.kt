package me.principality.backend.hbase

/**
 * 通过实现不同类型的表，优化查询性能
 * https://calcite.apache.org/docs/tutorial.html#optimizing-queries-using-planner-rules
 *
 * org.apache.calcite.schema 定义了几种常见的表
 *   org.apache.calcite.schema.ExtensibleTable
 *   org.apache.calcite.schema.FilterableTable
 *   org.apache.calcite.schema.ModifiableTable
 *   org.apache.calcite.schema.ProjectableFilterableTable
 *   org.apache.calcite.schema.QueryableTable
 *   org.apache.calcite.schema.ScannableTable
 *   org.apache.calcite.schema.StreamableTable
 *   org.apache.calcite.schema.TranslatableTable
 *
 * 关注：ScannableTable, FilterableTable, ProjectableFilterableTable, QueryableTable, TranslatableTable
 *
 * adapter负责对接calcite，最终的实现通过sharding来完成
 */
class HBaseTable {

}