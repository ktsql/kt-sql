package me.principality.ktsql.sqlexec.hbase

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedMap
import com.google.common.collect.ImmutableSortedSet
import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.schema.Function
import org.apache.calcite.schema.Schema
import org.apache.calcite.schema.SchemaVersion
import org.apache.calcite.schema.Table
import org.apache.calcite.util.NameMap
import org.apache.calcite.util.NameMultimap
import org.apache.calcite.util.NameSet

/**
 * 重写Schema，实现自定义Schema的支持
 */
class SqlSchema : CalciteSchema {
    constructor(parent: CalciteSchema,
                schema: Schema,
                name: String,
                subSchemaMap: NameMap<CalciteSchema>,
                tableMap: NameMap<TableEntry>,
                latticeMap: NameMap<LatticeEntry>,
                typeMap: NameMap<TypeEntry>,
                functionMap: NameMultimap<FunctionEntry>,
                functionNames: NameSet,
                nullaryFunctionMap: NameMap<FunctionEntry>,
                path: List<List<String>>) :
            super(parent, schema, name, subSchemaMap, tableMap, latticeMap,
                    typeMap, functionMap, functionNames, nullaryFunctionMap, path) {

    }

    override fun getImplicitTable(tableName: String?, caseSensitive: Boolean): TableEntry {
        TODO("not implemented")
    }

    override fun setCache(cache: Boolean) {
        TODO("not implemented")
    }

    override fun addImplicitFuncNamesToBuilder(builder: ImmutableSortedSet.Builder<String>?) {
        TODO("not implemented")
    }

    override fun getImplicitSubSchema(schemaName: String?, caseSensitive: Boolean): CalciteSchema {
        TODO("not implemented")
    }

    override fun addImplicitTableToBuilder(builder: ImmutableSortedSet.Builder<String>?) {
        TODO("not implemented")
    }

    override fun getImplicitTableBasedOnNullaryFunction(tableName: String?, caseSensitive: Boolean): TableEntry {
        TODO("not implemented")
    }

    override fun getImplicitType(name: String?, caseSensitive: Boolean): TypeEntry {
        TODO("not implemented")
    }

    override fun addImplicitTypeNamesToBuilder(builder: ImmutableSortedSet.Builder<String>?) {
        TODO("not implemented")
    }

    override fun isCacheEnabled(): Boolean {
        TODO("not implemented")
    }

    override fun add(name: String?, schema: Schema?): CalciteSchema {
        TODO("not implemented")
    }

    override fun addImplicitSubSchemaToBuilder(builder: ImmutableSortedMap.Builder<String, CalciteSchema>?) {
        TODO("not implemented")
    }

    override fun addImplicitTablesBasedOnNullaryFunctionsToBuilder(builder: ImmutableSortedMap.Builder<String, Table>?) {
        TODO("not implemented")
    }

    override fun snapshot(parent: CalciteSchema?, version: SchemaVersion?): CalciteSchema {
        TODO("not implemented")
    }

    override fun addImplicitFunctionsToBuilder(builder: ImmutableList.Builder<Function>?, name: String?, caseSensitive: Boolean) {
        TODO("not implemented")
    }
}