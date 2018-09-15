package me.principality.protocol.mysql.packet.command

import me.principality.backend.BackendPacketHandler
import me.principality.protocol.mysql.packet.MySQLPacket
import me.principality.protocol.mysql.packet.MySQLPacketPayload
import me.principality.protocol.mysql.packet.command.admin.ComInitDbPacket
import me.principality.protocol.mysql.packet.command.admin.ComPingPacket
import me.principality.protocol.mysql.packet.command.admin.ComQuitPacket
import me.principality.protocol.mysql.packet.command.query.*
import me.principality.sqlrewriter.SqlRewriterPacketHandler

object CommandPacketFactory {
    fun createCommandPacket(sequenceId: Int, connectionId: Int, payload: MySQLPacketPayload): CommandPacket {
        val commandPacketTypeValue = payload.readInt1()
        val type = CommandType.valueOf(commandPacketTypeValue)
        when (type) {
            CommandType.COM_QUIT ->
                return ComQuitPacket(sequenceId)
            CommandType.COM_INIT_DB ->
                return ComInitDbPacket(sequenceId, payload)
            CommandType.COM_FIELD_LIST ->
                return ComFieldListPacket(sequenceId, connectionId, payload, BackendPacketHandler())
            CommandType.COM_QUERY ->
                return ComQueryPacket(sequenceId, connectionId, payload, SqlRewriterPacketHandler())
            CommandType.COM_STMT_PREPARE ->
                return ComStmtPreparePacket(sequenceId, payload)
            CommandType.COM_STMT_EXECUTE ->
                return ComStmtExecutePacket(sequenceId, connectionId, payload, BackendPacketHandler())
            CommandType.COM_STMT_CLOSE ->
                return ComStmtClosePacket(sequenceId, payload)
            CommandType.COM_PING ->
                return ComPingPacket(sequenceId)
            CommandType.COM_SLEEP,
            CommandType.COM_CREATE_DB,
            CommandType.COM_DROP_DB,
            CommandType.COM_REFRESH,
            CommandType.COM_SHUTDOWN,
            CommandType.COM_STATISTICS,
            CommandType.COM_PROCESS_INFO,
            CommandType.COM_CONNECT,
            CommandType.COM_PROCESS_KILL,
            CommandType.COM_DEBUG,
            CommandType.COM_TIME,
            CommandType.COM_DELAYED_INSERT,
            CommandType.COM_CHANGE_USER,
            CommandType.COM_BINLOG_DUMP,
            CommandType.COM_TABLE_DUMP,
            CommandType.COM_CONNECT_OUT,
            CommandType.COM_REGISTER_SLAVE,
            CommandType.COM_STMT_SEND_LONG_DATA,
            CommandType.COM_STMT_RESET,
            CommandType.COM_SET_OPTION,
            CommandType.COM_STMT_FETCH,
            CommandType.COM_DAEMON,
            CommandType.COM_BINLOG_DUMP_GTID,
            CommandType.COM_RESET_CONNECTION ->
                return UnsupportedCommandPacket(sequenceId, type)
            else ->
                return UnsupportedCommandPacket(sequenceId, type)
        }
    }
}