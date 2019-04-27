package me.principality.ktsql.protocol.mysql.packet.constant

import java.sql.Types

enum class ColumnType(val value: Int) {

    MYSQL_TYPE_DECIMAL(0x00),
    MYSQL_TYPE_TINY(0x01),
    MYSQL_TYPE_SHORT(0x02),
    MYSQL_TYPE_LONG(0x03),
    MYSQL_TYPE_FLOAT(0x04),
    MYSQL_TYPE_DOUBLE(0x05),
    MYSQL_TYPE_NULL(0x06),
    MYSQL_TYPE_TIMESTAMP(0x07),
    MYSQL_TYPE_LONGLONG(0x08),
    MYSQL_TYPE_INT24(0x09),
    MYSQL_TYPE_DATE(0x0a),
    MYSQL_TYPE_TIME(0x0b),
    MYSQL_TYPE_DATETIME(0x0c),
    MYSQL_TYPE_YEAR(0x0d),
    MYSQL_TYPE_NEWDATE(0x0e),
    MYSQL_TYPE_VARCHAR(0x0f),
    MYSQL_TYPE_BIT(0x10),
    MYSQL_TYPE_TIMESTAMP2(0x11),
    MYSQL_TYPE_DATETIME2(0x12),
    MYSQL_TYPE_TIME2(0x13),
    MYSQL_TYPE_NEWDECIMAL(0xf6),
    MYSQL_TYPE_ENUM(0xf7),
    MYSQL_TYPE_SET(0xf8),
    MYSQL_TYPE_TINY_BLOB(0xf9),
    MYSQL_TYPE_MEDIUM_BLOB(0xfa),
    MYSQL_TYPE_LONG_BLOB(0xfb),
    MYSQL_TYPE_BLOB(0xfc),
    MYSQL_TYPE_VAR_STRING(0xfd),
    MYSQL_TYPE_STRING(0xfe),
    MYSQL_TYPE_GEOMETRY(0xff);

    companion object {
        /**
         * Value of JDBC type.
         *
         * @param jdbcType JDBC type
         * @return column type enum
         */
        fun valueOfJDBCType(jdbcType: Int): ColumnType {
            when (jdbcType) {
                Types.BIT -> return MYSQL_TYPE_BIT
                Types.TINYINT -> return MYSQL_TYPE_TINY
                Types.SMALLINT -> return MYSQL_TYPE_SHORT
                Types.INTEGER -> return MYSQL_TYPE_LONG
                Types.BIGINT -> return MYSQL_TYPE_LONGLONG
                Types.FLOAT -> return MYSQL_TYPE_FLOAT
                Types.REAL -> return MYSQL_TYPE_FLOAT
                Types.DOUBLE -> return MYSQL_TYPE_DOUBLE
                Types.NUMERIC -> return MYSQL_TYPE_NEWDECIMAL
                Types.DECIMAL -> return MYSQL_TYPE_NEWDECIMAL
                Types.CHAR -> return MYSQL_TYPE_VARCHAR
                Types.VARCHAR -> return MYSQL_TYPE_VARCHAR
                Types.LONGVARCHAR -> return MYSQL_TYPE_VARCHAR
                Types.DATE -> return MYSQL_TYPE_DATE
                Types.TIME -> return MYSQL_TYPE_TIME
                Types.TIMESTAMP -> return MYSQL_TYPE_TIMESTAMP
                Types.BINARY -> return MYSQL_TYPE_BLOB
                Types.VARBINARY -> return MYSQL_TYPE_MEDIUM_BLOB
                Types.LONGVARBINARY -> return MYSQL_TYPE_LONG_BLOB
                Types.NULL -> return MYSQL_TYPE_NULL
                Types.BLOB -> return MYSQL_TYPE_BLOB
                else -> throw IllegalArgumentException(String.format("Cannot find JDBC type '%s' in column type", jdbcType))
            }
        }

        /**
         * Value of.
         *
         * @param value value
         * @return column type
         */
        fun valueOf(value: Int): ColumnType {
            for (each in ColumnType.values()) {
                if (value == each.value) {
                    return each
                }
            }
            throw IllegalArgumentException(String.format("Cannot find value '%s' in column type", value))
        }
    }
}