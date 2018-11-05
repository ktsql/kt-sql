package org.apache.calcite.jdbc

// hack to access java protected function

import me.principality.ktsql.utils.config.Version
import org.apache.calcite.adapter.java.JavaTypeFactory
import org.apache.calcite.avatica.*
import org.apache.calcite.jdbc.CalciteConnectionImpl
import org.apache.calcite.jdbc.CalciteStatement
import org.apache.calcite.jdbc.SqlSchema
import java.io.InputStream
import java.io.Reader
import java.sql.*
import java.util.*

/**
 * SqlDriver会在jdbc调用时，完成SqlFactory的初始化，并保存在protected final AvaticaFactory factory变量中，
 * SqlFactory继承AvaticaFactory，沿用现有实现，同时改写newConnection，把定制的Schema传进去
 */
class SqlFactory : AvaticaFactory {
    protected val major: Int = Version.major.toInt()
    protected val minor: Int = Version.minor.toInt()

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
        val rootSchema = SqlSchema(null, null, "")
        return SqlJdbc41Connection(
                driver as Driver, factory, url, info, rootSchema, null)
    }

    override fun newStatement(connection: AvaticaConnection?,
                              h: Meta.StatementHandle,
                              resultSetType: Int,
                              resultSetConcurrency: Int,
                              resultSetHoldability: Int): AvaticaStatement {
        return SqlJdbc41Statement(
                connection as CalciteConnectionImpl,
                h,
                resultSetType, resultSetConcurrency,
                resultSetHoldability)
    }

    override fun newResultSetMetaData(statement: AvaticaStatement?,
                                      signature: Meta.Signature?): ResultSetMetaData {
        return AvaticaResultSetMetaData(statement, null, signature)
    }

    override fun newPreparedStatement(connection: AvaticaConnection?,
                                      h: Meta.StatementHandle,
                                      signature: Meta.Signature?,
                                      resultSetType: Int,
                                      resultSetConcurrency: Int,
                                      resultSetHoldability: Int): AvaticaPreparedStatement {
        return SqlJdbc41PreparedStatement(
                connection as CalciteConnectionImpl, h,
                signature as CalcitePrepare.CalciteSignature<*>, resultSetType,
                resultSetConcurrency, resultSetHoldability)
    }

    override fun newResultSet(statement: AvaticaStatement?,
                              state: QueryState?,
                              signature: Meta.Signature?,
                              timeZone: TimeZone?,
                              firstFrame: Meta.Frame?): AvaticaResultSet {
        val metaData = newResultSetMetaData(statement, signature)
        val calciteSignature = signature as CalcitePrepare.CalciteSignature<*>
        return CalciteResultSet(statement, calciteSignature, metaData, timeZone,
                firstFrame)
    }

    override fun newDatabaseMetaData(connection: AvaticaConnection?): AvaticaSpecificDatabaseMetaData {
        return SqlJdbc41DatabaseMetaData(
                connection as CalciteConnectionImpl)
    }

    internal class SqlJdbc41Connection constructor(driver: Driver, factory: AvaticaFactory, url: String,
                                                   info: Properties, rootSchema: CalciteSchema,
                                                   typeFactory: JavaTypeFactory?) :
            CalciteConnectionImpl(driver, factory, url, info, rootSchema, typeFactory)

    internal class SqlJdbc41Statement : CalciteStatement {
        constructor(connection: CalciteConnectionImpl,
                    h: Meta.StatementHandle, resultSetType: Int,
                    resultSetConcurrency: Int,
                    resultSetHoldability: Int) :
                super(connection, h, resultSetType, resultSetConcurrency, resultSetHoldability) {

        }
    }

    internal class SqlJdbc41PreparedStatement @Throws(SQLException::class)
    constructor(connection: CalciteConnectionImpl,
                h: Meta.StatementHandle, signature: CalcitePrepare.CalciteSignature<*>,
                resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int) :
            CalcitePreparedStatement(connection, h, signature, resultSetType,
                    resultSetConcurrency, resultSetHoldability) {

        @Throws(SQLException::class)
        override fun setRowId(
                parameterIndex: Int,
                x: RowId) {
            getSite(parameterIndex).setRowId(x)
        }

        @Throws(SQLException::class)
        override fun setNString(
                parameterIndex: Int, value: String) {
            getSite(parameterIndex).setNString(value)
        }

        @Throws(SQLException::class)
        override fun setNCharacterStream(
                parameterIndex: Int,
                value: Reader,
                length: Long) {
            getSite(parameterIndex)
                    .setNCharacterStream(value, length)
        }

        @Throws(SQLException::class)
        override fun setNClob(
                parameterIndex: Int,
                value: NClob) {
            getSite(parameterIndex).setNClob(value)
        }

        @Throws(SQLException::class)
        override fun setClob(
                parameterIndex: Int,
                reader: Reader,
                length: Long) {
            getSite(parameterIndex)
                    .setClob(reader, length)
        }

        @Throws(SQLException::class)
        override fun setBlob(
                parameterIndex: Int,
                inputStream: InputStream,
                length: Long) {
            getSite(parameterIndex)
                    .setBlob(inputStream, length)
        }

        @Throws(SQLException::class)
        override fun setNClob(
                parameterIndex: Int,
                reader: Reader,
                length: Long) {
            getSite(parameterIndex).setNClob(reader, length)
        }

        @Throws(SQLException::class)
        override fun setSQLXML(
                parameterIndex: Int, xmlObject: SQLXML) {
            getSite(parameterIndex).setSQLXML(xmlObject)
        }

        @Throws(SQLException::class)
        override fun setAsciiStream(
                parameterIndex: Int,
                x: InputStream,
                length: Long) {
            getSite(parameterIndex)
                    .setAsciiStream(x, length)
        }

        @Throws(SQLException::class)
        override fun setBinaryStream(
                parameterIndex: Int,
                x: InputStream,
                length: Long) {
            getSite(parameterIndex)
                    .setBinaryStream(x, length)
        }

        @Throws(SQLException::class)
        override fun setCharacterStream(
                parameterIndex: Int,
                reader: Reader,
                length: Long) {
            getSite(parameterIndex)
                    .setCharacterStream(reader, length)
        }

        @Throws(SQLException::class)
        override fun setAsciiStream(
                parameterIndex: Int, x: InputStream) {
            getSite(parameterIndex).setAsciiStream(x)
        }

        @Throws(SQLException::class)
        override fun setBinaryStream(
                parameterIndex: Int, x: InputStream) {
            getSite(parameterIndex).setBinaryStream(x)
        }

        @Throws(SQLException::class)
        override fun setCharacterStream(
                parameterIndex: Int, reader: Reader) {
            getSite(parameterIndex)
                    .setCharacterStream(reader)
        }

        @Throws(SQLException::class)
        override fun setNCharacterStream(
                parameterIndex: Int, value: Reader) {
            getSite(parameterIndex)
                    .setNCharacterStream(value)
        }

        @Throws(SQLException::class)
        override fun setClob(
                parameterIndex: Int,
                reader: Reader) {
            getSite(parameterIndex).setClob(reader)
        }

        @Throws(SQLException::class)
        override fun setBlob(
                parameterIndex: Int, inputStream: InputStream) {
            getSite(parameterIndex)
                    .setBlob(inputStream)
        }

        @Throws(SQLException::class)
        override fun setNClob(
                parameterIndex: Int, reader: Reader) {
            getSite(parameterIndex)
                    .setNClob(reader)
        }
    }

    private class SqlJdbc41DatabaseMetaData internal constructor(connection: CalciteConnectionImpl) :
            AvaticaDatabaseMetaData(connection)
}