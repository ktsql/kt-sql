package org.apache.calcite.jdbc // hack to access java protected function

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedMap
import com.google.common.collect.ImmutableSortedSet
import me.principality.ktsql.utils.collections.RemoteNameMap
import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.schema.Function
import org.apache.calcite.schema.Schema
import org.apache.calcite.schema.SchemaVersion
import org.apache.calcite.schema.Table
import org.apache.calcite.util.NameMap
import org.apache.calcite.util.NameMultimap
import org.apache.calcite.util.NameSet
import org.apache.calcite.schema.TableMacro
import org.apache.calcite.rel.type.RelProtoDataType

/**
 * 重写Schema，实现自定义Schema的支持
 *
 * 需要了解SchemaFactory创建出来的Schema，是否等价于SqlSchema？<= 不等价
 *
 * 通过calcite代码跟踪确认，CalciteSchema没有实现添加表的持久化操作，
 * 参考 TableEntry add(String tableName, Table table, ImmutableList<String> sqls)。
 * 所以需要修改CalciteSchema，支持从schema创建的表持久化
 *
 * 另外，还需要实现表创建的操作，这通过adaptor下的schema完成（目前adaptor->schema只支持getTable）
 *
 * 这一修改需要变动：
 * 1. 将calcite-server createTable()逻辑，修改为从adapter创建
 * 2. SqlSchema添加对adaptor-schema的支持
 *
 * 持久化的添加表、索引等操作，可通过子类继承改写来实现
 *
 * CalciteSchema在一个单机环境，采用本机内存缓存上下文信息是可行的，但是在一个分布式的环境，
 * 必须将元数据的访问独立出去，以便保持一致
 *
 * 元数据的管理策略有两种：一种是有更新的时候，通知到所有的节点；
 * 另外一种是本地不缓存，每次都去中心服务器获取。这两种策略都需要考虑一致性和性能问题。
 *
 * 初步的方案是把数据都放在系统表上，另外drop table和drop index，要特别小心，注意一致性问题
 */
class SqlSchema : CalciteSchema {
    protected val remoteTableMap: RemoteNameMap<TableEntry>
    protected val remoteTypeMap: RemoteNameMap<TypeEntry>

    constructor(parent: CalciteSchema?, schema: Schema?, name: String) :
            this(parent, schema, name, null, null,
                    null, null, null, null,
                    null, null) {
    }

    constructor(parent: CalciteSchema?,
                schema: Schema?,
                name: String,
                subSchemaMap: NameMap<CalciteSchema>?,
                tableMap: NameMap<TableEntry>?,
                latticeMap: NameMap<LatticeEntry>?,
                typeMap: NameMap<TypeEntry>?,
                functionMap: NameMultimap<FunctionEntry>?,
                functionNames: NameSet?,
                nullaryFunctionMap: NameMap<FunctionEntry>?,
                path: List<List<String>>?) :
            super(parent, schema, name, subSchemaMap, tableMap, latticeMap,
                    typeMap, functionMap, functionNames, nullaryFunctionMap, path) {
        remoteTableMap = RemoteNameMap<TableEntry>("")
        remoteTypeMap = RemoteNameMap<TypeEntry>("")
    }

    override fun getImplicitTable(tableName: String?, caseSensitive: Boolean): TableEntry? {
        val table = schema.getTable(tableName)
        return if (table != null) {
            tableEntry(tableName, table)
        } else null
    }

    override fun setCache(cache: Boolean) {
        TODO("not implemented")
    }

    override fun addImplicitFuncNamesToBuilder(builder: ImmutableSortedSet.Builder<String>?) {
        builder!!.addAll(schema.getFunctionNames())
    }

    override fun getImplicitSubSchema(schemaName: String?, caseSensitive: Boolean): CalciteSchema? {
        val s = schema.getSubSchema(schemaName)
        return if (s != null) {
            SqlSchema(this, s, schemaName!!)
        } else null
    }

    override fun addImplicitTableToBuilder(builder: ImmutableSortedSet.Builder<String>?) {
        builder!!.addAll(schema.getTableNames())
    }

    override fun getImplicitTableBasedOnNullaryFunction(tableName: String?, caseSensitive: Boolean): TableEntry? {
        val functions = schema.getFunctions(tableName)
        if (functions != null) {
            for (function in functions) {
                if (function is TableMacro && function.getParameters().isEmpty()) {
                    val table = (function as TableMacro).apply(ImmutableList.of())
                    return tableEntry(tableName, table)
                }
            }
        }
        return null
    }

    override fun getImplicitType(name: String?, caseSensitive: Boolean): TypeEntry? {
        val type = schema.getType(name)
        return if (type != null) {
            typeEntry(name, type)
        } else null
    }

    override fun addImplicitTypeNamesToBuilder(builder: ImmutableSortedSet.Builder<String>?) {
        builder!!.addAll(schema.getTypeNames())
    }

    override fun isCacheEnabled(): Boolean {
        return false
    }

    override fun add(name: String?, schema: Schema?): CalciteSchema {
        val calciteSchema = SqlSchema(this, schema!!, name!!)
        subSchemaMap.put(name, calciteSchema)
        return calciteSchema
    }

    override fun addImplicitSubSchemaToBuilder(builder: ImmutableSortedMap.Builder<String, CalciteSchema>?) {
        val explicitSubSchemas = builder!!.build()
        for (schemaName in schema.subSchemaNames) {
            if (explicitSubSchemas.containsKey(schemaName)) {
                // explicit subschema wins.
                continue
            }
            val s = schema.getSubSchema(schemaName)
            if (s != null) {
                val calciteSchema = SqlSchema(this, s, schemaName)
                builder.put(schemaName, calciteSchema)
            }
        }
    }

    override fun addImplicitTablesBasedOnNullaryFunctionsToBuilder(builder: ImmutableSortedMap.Builder<String, Table>?) {
        val explicitTables = builder!!.build()

        for (s in schema.functionNames) {
            // explicit table wins.
            if (explicitTables.containsKey(s)) {
                continue
            }
            for (function in schema.getFunctions(s)) {
                if (function is TableMacro && function.getParameters().isEmpty()) {
                    val table = function.apply(ImmutableList.of())
                    builder.put(s, table)
                }
            }
        }
    }

    override fun snapshot(parent: CalciteSchema?, version: SchemaVersion?): CalciteSchema {
        val snapshot = SqlSchema(parent!!,
                schema.snapshot(version), name, null, tableMap, latticeMap, typeMap,
                functionMap, functionNames, nullaryFunctionMap, path)
        for (subSchema in subSchemaMap.map().values) {
            val subSchemaSnapshot = subSchema.snapshot(snapshot, version)
            snapshot.subSchemaMap.put(subSchema.name, subSchemaSnapshot)
        }
        return snapshot
    }

    override fun addImplicitFunctionsToBuilder(builder: ImmutableList.Builder<Function>?,
                                               name: String?, caseSensitive: Boolean) {
        val functions = schema.getFunctions(name)
        if (functions != null) {
            builder!!.addAll(functions)
        }
    }
}