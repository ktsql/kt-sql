package me.principality.ktsql.protocol.mysql.packet.command.query

import me.principality.ktsql.protocol.mysql.packet.command.CommandResponsePackets
import me.principality.ktsql.protocol.mysql.packet.constant.ColumnType
import me.principality.ktsql.protocol.mysql.packet.generic.EofPacket
import java.util.*

class QueryResponsePackets: CommandResponsePackets {
    private var fieldCountPacket: FieldCountPacket
    private var columnDefinition41Packets: Collection<ColumnDefinition41Packet>

    constructor(fieldCountPacket: FieldCountPacket,
                columnDefinition41Packets: Collection<ColumnDefinition41Packet>,
                eofPacket: EofPacket) {
        packets.add(fieldCountPacket)
        packets.addAll(columnDefinition41Packets)
        packets.add(eofPacket)
        this.fieldCountPacket = fieldCountPacket
        this.columnDefinition41Packets = columnDefinition41Packets
    }

    /**
     * Get column count.
     *
     * @return column count
     */
    fun getColumnCount(): Int {
        return fieldCountPacket.getColumnCount()
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
    }}