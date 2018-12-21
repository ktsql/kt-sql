package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.ktsql.protocol.mysql.packet.constant.ColumnType
import me.principality.ktsql.protocol.mysql.packet.generic.EofPacket
import java.util.*

/**
 * https://dev.mysql.com/doc/internals/en/com-query-response.html#packet-COM_QUERY_Response
 * https://mariadb.com/kb/en/library/resultset/
 *
 * ComQueryResponsePacket会返回以下几个类型的包，客户端是通过packet head来判断并进行处理的：
 * 1. ErrPacket
 * 2. OkPacket
 * 3. LocalInFilePacket
 * 4. ResultSet (QueryResponsePackets)
 *
 * 对select操作，返回的是QueryResponsePackets，包含ZeroOrMore个结果
 *
 * ResultSet由多个Packet组成
 * - ResultSet MetaData
 *   - 1 Column count packet
 *   - n Column Definition packet
 * - if not (CLIENT_DEPRECATE_EOF capability set) EOF_Packet
 * - n resultset row
 * - if error ERR_Packet
 * - else (if CLIENT_DEPRECATE_EOF capability OK_Packet with a 0xFE header)
 * -      (else EOF_Packet)
 *
 * 因为简化的原因，所以默认：not CLIENT_DEPRECATE_EOF
 *
 * QueryResponsePackets
 * - ColumnCountPacket
 * - N ColumnDefinition41Packet
 * - EofPacket
 * TextResultSetRowPacket
 * EofPacket
 *
 * 往客户端写查询响应由MySQLHandler负责，回写的逻辑是：
 * 1. 把QueryResponsePackets往客户端写
 * 2. 如果是QueryCommand，查询的结果保存在QueryCommand里面，把QueryCommand往客户端写
 */
class QueryResponsePackets : CommandResponsePackets {
    private var columnCountPacket: ColumnCountPacket
    private var columnDefinition41Packets: Collection<ColumnDefinition41Packet>

    constructor(columnCountPacket: ColumnCountPacket,
                columnDefinition41Packets: Collection<ColumnDefinition41Packet>,
                eofPacket: EofPacket) {
        packets.add(columnCountPacket)
        packets.addAll(columnDefinition41Packets)
        packets.add(eofPacket)
        this.columnCountPacket = columnCountPacket
        this.columnDefinition41Packets = columnDefinition41Packets
    }

    /**
     * Get column count.
     *
     * @return column count
     */
    fun getColumnCount(): Int {
        return columnCountPacket.getColumnCount()
    }

    /**
     * Get column types.
     *
     * @return column types
     */
    fun getColumnTypes(): List<ColumnType> {
        val result = LinkedList<ColumnType>()
        for (each in columnDefinition41Packets) {
            result.add(each.columnType)
        }
        return result
    }
}