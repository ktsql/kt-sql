package me.principality.ktsql.protocol.mysql.packet

import com.google.common.base.Strings
import io.vertx.core.buffer.Buffer
import java.sql.Timestamp
import java.util.*

/**
 * TODO 1. 保存在JVM的Packet，对包很大的情况，会出现内存不足的问题，有必要限制并发的连接数
 * TODO 2. 采用JVM堆内存进行管理的packet，长时间运行会有GC压力，需考虑调优
 * TODO 3. vertx的buffer使用堆外内存实现，但如何回收需要进一步了解
 */
class MySQLPacketPayload {
    val byteBuffer: Buffer
    var position: Int = 0
    var id: Int = 0
    var size: Int = 0

    constructor(size: Int, packetId: Int) {
        byteBuffer = Buffer.buffer(size + 4)
        this.size = size
        this.id = packetId
    }

    constructor(buff: Buffer) {
        byteBuffer = buff
    }

    fun readInt1(): Int {
        val tmp = position
        position++
        return byteBuffer.getByte(tmp).toInt() and 0xff
    }

    fun writeInt1(value: Int) {
        byteBuffer.appendByte(value.toByte())
    }

    fun readInt2(): Int {
        val tmp = position
        position += 2
        return byteBuffer.getShortLE(tmp).toInt()
    }

    fun writeInt2(value: Int) {
        byteBuffer.appendShortLE(value.toShort())
    }

    fun readInt3(): Int {
        val tmp = position
        position += 3
        return byteBuffer.getMediumLE(tmp) and 0xffffff
    }

    fun writeInt3(value: Int) {
        byteBuffer.appendMediumLE(value)
    }

    fun readInt4(): Int {
        val tmp = position
        position += 4
        return byteBuffer.getIntLE(tmp)
    }

    fun writeInt4(value: Int) {
        byteBuffer.appendIntLE(value)
    }

    fun readInt6(): Int {
        // TODO
        return 0
    }

    fun writeInt6(value: Int) {
        // TODO
    }

    fun readInt8(): Long {
        val tmp = position
        position += 8
        return byteBuffer.getLongLE(tmp)
    }

    fun writeInt8(value: Long) {
        byteBuffer.appendLongLE(value)

    }

    fun readIntLenenc(): Long {
        val firstByte = readInt1()
        if (firstByte < 0xfb) {
            return firstByte.toLong()
        }
        if (0xfb == firstByte) {
            return 0
        }
        if (0xfc == firstByte) {
            val tmp = position
            position += 2
            return byteBuffer.getShortLE(tmp).toLong()
        }
        return if (0xfd == firstByte) {
            val tmp = position
            position += 2
            byteBuffer.getMediumLE(tmp).toLong()
        } else {
            val tmp = position
            position += 2
            byteBuffer.getLongLE(tmp)
        }
    }

    fun writeIntLenenc(value: Long) {
        if (value < 251) {
            byteBuffer.appendByte(value.toByte())
            return
        }
        if (value < Math.pow(2.0, 16.0)) {
            byteBuffer.appendUnsignedByte(0xfc)
            byteBuffer.appendShortLE(value.toShort())
            return
        }
        if (value < Math.pow(2.0, 24.0)) {
            byteBuffer.appendUnsignedByte(0xfd)
            byteBuffer.appendInt(value.toInt())
            return
        }
        byteBuffer.appendUnsignedByte(0xfe)
        byteBuffer.appendLongLE(value)
    }

    fun readStringLenenc(): String {
        val length = readIntLenenc().toInt()
        val result = ByteArray(length)
        position += result.size
        byteBuffer.getBytes(result)
        return String(result)
    }

    fun readStringLenencByBytes(): ByteArray {
        val length = readIntLenenc().toInt()
        val result = ByteArray(length)
        position += result.size
        byteBuffer.getBytes(result)
        return result
    }

    fun writeStringLenenc(value: String) {
        if (Strings.isNullOrEmpty(value)) {
            byteBuffer.appendByte(0)
            return
        }
        writeIntLenenc(value.toByteArray().size.toLong())
        byteBuffer.appendBytes(value.toByteArray())
    }

    fun writeBytesLenenc(value: ByteArray) {
        if (0 == value.size) {
            byteBuffer.appendByte(0)
            return
        }
        writeIntLenenc(value.size.toLong())
        byteBuffer.appendBytes(value)
    }

    fun readStringFix(length: Int): String {
        val result = ByteArray(length)
        byteBuffer.getBytes(position, position + length, result)
        position += result.size
        return String(result)
    }

    fun readStringFixByBytes(length: Int): ByteArray {
        val result = ByteArray(length)
        byteBuffer.getBytes(position, position + length, result)
        position += result.size
        return result
    }

    fun writeStringFix(value: String) {
        byteBuffer.appendBytes(value.toByteArray())
    }

    fun writeBytes(value: ByteArray) {
        byteBuffer.appendBytes(value)
    }

    fun readStringVar(): String {
        // TODO
        return ""
    }

    fun writeStringVar(value: String) {
        // TODO
    }

    fun Buffer.bytesBefore(byte: Byte): Int {
        var index = position
        while (this.getByte(index) != byte && index < this.length() ) {
            index++
        }
        if (index > this.length())
            return 0

        return index - position
    }

    fun readStringNul(): String {
        val result = ByteArray(byteBuffer.bytesBefore(0.toByte()))
        byteBuffer.getBytes(position, position + result.size, result)
        position += result.size + 1
        return String(result)
    }

    fun readStringNulByBytes(): ByteArray {
        val result = ByteArray(byteBuffer.bytesBefore(0.toByte()))
        byteBuffer.getBytes(position, position + result.size, result)
        position += result.size + 1
        return result
    }

    fun writeStringNul(value: String) {
        byteBuffer.appendBytes(value.toByteArray())
        byteBuffer.appendByte(0)
    }

    fun readStringEOF(): String {
        val result = ByteArray(byteBuffer.length() - position)
        byteBuffer.getBytes(position, byteBuffer.length(), result)
        position += result.size
        return String(result)
    }

    fun writeStringEOF(value: String) {
        byteBuffer.appendBytes(value.toByteArray())
    }

    fun skipReserved(length: Int) {
        position += length
    }

    fun writeReserved(length: Int) {
        for (i in 0 until length) {
            byteBuffer.appendByte(0)
        }
    }

    fun readFloat(): Float {
        val tmp = position
        position += 4
        return byteBuffer.getFloat(tmp)
    }

    fun writeFloat(value: Float) {
        byteBuffer.appendFloat(value)
    }

    fun readDouble(): Double {
        val tmp = position
        position += 8
        return byteBuffer.getDouble(tmp)
    }

    fun writeDouble(value: Double) {
        byteBuffer.appendDouble(value)
    }

    fun readDate(): Timestamp {
        val result: Timestamp
        val calendar = Calendar.getInstance()
        val length = readInt1()
        when (length) {
            0 -> result = Timestamp(0)
            4 -> {
                calendar.set(readInt2(), readInt1() - 1, readInt1())
                result = Timestamp(calendar.timeInMillis)
            }
            7 -> {
                calendar.set(readInt2(), readInt1() - 1, readInt1(), readInt1(), readInt1(), readInt1())
                result = Timestamp(calendar.timeInMillis)
            }
            11 -> {
                calendar.set(readInt2(), readInt1() - 1, readInt1(), readInt1(), readInt1(), readInt1())
                result = Timestamp(calendar.timeInMillis)
                result.nanos = readInt4()
            }
            else -> throw IllegalArgumentException(String.format("Wrong length '%d' of MYSQL_TYPE_TIME", length))
        }
        return result
    }

    fun writeDate(timestamp: Timestamp) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp.time
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val millisecond = timestamp.nanos
        val isDateValueAbsent = 0 == year && 0 == month && 0 == day
        val isTimeValueAbsent = 0 == hour && 0 == minute && 0 == second
        val isMillisecondValueAbsent = 0 == millisecond
        if (isDateValueAbsent && isTimeValueAbsent && isMillisecondValueAbsent) {
            writeInt1(0)
        } else if (isTimeValueAbsent && isMillisecondValueAbsent) {
            writeInt1(4)
            writeInt2(year)
            writeInt1(month)
            writeInt1(day)
        } else if (isMillisecondValueAbsent) {
            writeInt1(7)
            writeInt2(year)
            writeInt1(month)
            writeInt1(day)
            writeInt1(hour)
            writeInt1(minute)
            writeInt1(second)
        } else {
            writeInt1(11)
            writeInt2(year)
            writeInt1(month)
            writeInt1(day)
            writeInt1(hour)
            writeInt1(minute)
            writeInt1(second)
            writeInt4(millisecond)
        }
    }

    fun readTime(): Timestamp {
        val result: Timestamp
        val calendar = Calendar.getInstance()
        val length = readInt1()
        readInt1()
        readInt4()
        when (length) {
            0 -> result = Timestamp(0)
            8 -> {
                calendar.set(0, Calendar.JANUARY, 0, readInt1(), readInt1(), readInt1())
                result = Timestamp(calendar.timeInMillis)
                result.nanos = 0
            }
            12 -> {
                calendar.set(0, Calendar.JANUARY, 0, readInt1(), readInt1(), readInt1())
                result = Timestamp(calendar.timeInMillis)
                result.nanos = readInt4()
            }
            else -> throw IllegalArgumentException(String.format("Wrong length '%d' of MYSQL_TYPE_DATE", length))
        }
        return result
    }

    fun writeTime(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date.time
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val timestamp = Timestamp(date.time)
        val millisecond = timestamp.nanos
        val isTimeValueAbsent = 0 == hour && 0 == minute && 0 == second
        val isMillisecondValueAbsent = 0 == millisecond
        if (isTimeValueAbsent && isMillisecondValueAbsent) {
            writeInt1(0)
        } else if (isMillisecondValueAbsent) {
            writeInt1(8)
            writeInt1(0)
            writeInt4(0)
            writeInt1(hour)
            writeInt1(minute)
            writeInt1(second)
        } else {
            writeInt1(12)
            writeInt1(0)
            writeInt4(0)
            writeInt1(hour)
            writeInt1(minute)
            writeInt1(second)
            writeInt4(millisecond)
        }
    }
}
