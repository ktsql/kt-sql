package me.principality.ktsql.sqlexec.hbase

import org.apache.calcite.avatica.*
import java.sql.ResultSetMetaData
import java.util.*

/**
 * SqlDriver会在jdbc调用时，完成SqlFactory的初始化，并保存在protected final AvaticaFactory factory变量中
 * SqlFactory继承AvaticaFactory，沿用现有实现，同时改写newConnection，把定制的Schema传进去
 */
class SqlFactory : AvaticaFactory {
    protected val major: Int = 0
    protected val minor: Int = 1

    private lateinit var schema: SqlSchema

    override fun getJdbcMajorVersion(): Int {
        return major
    }

    override fun getJdbcMinorVersion(): Int {
        return minor
    }

    override fun newConnection(
            driver: UnregisteredDriver,
            factory: AvaticaFactory,
            url: String,
            info: Properties): AvaticaConnection {
        TODO() // 参考CalciteJdbc41Factory完成实现
    }

    override fun newStatement(connection: AvaticaConnection?,
                              h: Meta.StatementHandle?,
                              resultSetType: Int,
                              resultSetConcurrency: Int,
                              resultSetHoldability: Int): AvaticaStatement {
        TODO("not implemented")
    }

    override fun newResultSetMetaData(statement: AvaticaStatement?,
                                      signature: Meta.Signature?): ResultSetMetaData {
        TODO("not implemented")
    }

    override fun newPreparedStatement(connection: AvaticaConnection?,
                                      h: Meta.StatementHandle?,
                                      signature: Meta.Signature?,
                                      resultSetType: Int,
                                      resultSetConcurrency: Int,
                                      resultSetHoldability: Int): AvaticaPreparedStatement {
        TODO("not implemented")
    }

    override fun newResultSet(statement: AvaticaStatement?,
                              state: QueryState?,
                              signature: Meta.Signature?,
                              timeZone: TimeZone?,
                              firstFrame: Meta.Frame?): AvaticaResultSet {
        TODO("not implemented")
    }

    override fun newDatabaseMetaData(connection: AvaticaConnection?): AvaticaSpecificDatabaseMetaData {
        TODO("not implemented")
    }
}