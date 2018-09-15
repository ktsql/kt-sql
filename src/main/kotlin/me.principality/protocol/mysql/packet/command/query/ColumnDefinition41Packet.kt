package me.principality.protocol.mysql.packet.command.query

import com.google.common.base.Preconditions
import jdk.nashorn.internal.objects.annotations.Getter
import me.principality.protocol.mysql.packet.MySQLPacket
import me.principality.protocol.mysql.packet.MySQLPacketPayload
import me.principality.protocol.mysql.packet.constant.ColumnType
import me.principality.protocol.mysql.packet.constant.ServerInfo
import java.sql.ResultSetMetaData
import java.sql.SQLException

class ColumnDefinition41Packet : MySQLPacket {
    private val CATALOG = "def"
    private val NEXT_LENGTH: Long = 0x0c
    private var sequenceId: Int = 0
    private var characterSet: Int = 0
    private var flags: Int = 0
    private var schema: String = ""
    private var table: String = ""
    private var orgTable: String = ""
    private var name: String = ""
    private var orgName: String = ""
    private var columnLength: Int = 0
    var columnType: ColumnType = ColumnType.MYSQL_TYPE_NULL
        private set
    private var decimals: Int = 0

    @Throws(SQLException::class)
    constructor(sequenceId: Int, resultSetMetaData: ResultSetMetaData, columnIndex: Int) {
        ColumnDefinition41Packet(sequenceId, resultSetMetaData.getSchemaName(columnIndex),
                resultSetMetaData.getTableName(columnIndex), resultSetMetaData.getTableName(columnIndex),
                resultSetMetaData.getColumnLabel(columnIndex), resultSetMetaData.getColumnName(columnIndex),
                resultSetMetaData.getColumnDisplaySize(columnIndex),
                ColumnType.valueOfJDBCType(resultSetMetaData.getColumnType(columnIndex)), 0)
    }

    constructor(sequenceId: Int, schema: String, table: String, orgTable: String,
                                 name: String, orgName: String, columnLength: Int,
                                 columnType: ColumnType, decimals: Int) {
        this.sequenceId = sequenceId
        this.characterSet = ServerInfo.CHARSET
        this.flags = 0
        this.schema = schema
        this.table = table
        this.orgTable = orgTable
        this.name = name
        this.orgName = orgName
        this.columnLength = columnLength
        this.columnType = columnType
        this.decimals = decimals
    }

    constructor(payload: MySQLPacketPayload) {
        sequenceId = payload.readInt1()
        Preconditions.checkArgument(CATALOG == payload.readStringLenenc())
        schema = payload.readStringLenenc()
        table = payload.readStringLenenc()
        orgTable = payload.readStringLenenc()
        name = payload.readStringLenenc()
        orgName = payload.readStringLenenc()
        Preconditions.checkArgument(NEXT_LENGTH == payload.readIntLenenc())
        characterSet = payload.readInt2()
        columnLength = payload.readInt4()
        columnType = ColumnType.valueOf(payload.readInt1())
        flags = payload.readInt2()
        decimals = payload.readInt1()
        payload.skipReserved(2)
    }

    override fun getSequenceId(): Int {
        return sequenceId
    }

    override fun getPacketSize(): Int {
        return (CATALOG.length
                + schema.length
                + table.length
                + orgTable.length
                + name.length
                + orgName.length
                + 4
                + 2
                + 4
                + 1
                + 2
                + 1
                + 2
                )
    }

    override fun writeTo(payload: MySQLPacketPayload): MySQLPacketPayload {
        payload.writeStringLenenc(CATALOG)
        payload.writeStringLenenc(schema)
        payload.writeStringLenenc(table)
        payload.writeStringLenenc(orgTable)
        payload.writeStringLenenc(name)
        payload.writeStringLenenc(orgName)
        payload.writeIntLenenc(NEXT_LENGTH)
        payload.writeInt2(characterSet)
        payload.writeInt4(columnLength)
        payload.writeInt1(columnType.value)
        payload.writeInt2(flags)
        payload.writeInt1(decimals)
        payload.writeReserved(2)
        return payload
    }
}