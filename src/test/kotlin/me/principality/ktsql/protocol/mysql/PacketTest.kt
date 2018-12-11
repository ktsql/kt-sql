package me.principality.ktsql.protocol.mysql

import me.principality.ktsql.protocol.mysql.packet.constant.ServerErrorCode
import org.junit.Test

class PacketTest {

    /**
     * kotlin 对 vararg 转换有问题
     */
    @Test
    fun testStringFmt() {
        fun fmtStr(code: ServerErrorCode, vararg errorMessageArguments: Any): String {
            val f = code.reason
            val s = java.lang.String.format(f, errorMessageArguments)
            return s
        }

        val f = ServerErrorCode.ER_ACCESS_DENIED_ERROR.reason
        val s1 = String.format(f, "user", "local", "pass")
        val s2 = fmtStr(ServerErrorCode.ER_ACCESS_DENIED_ERROR, "user", "local", "pass")

        println(s1)
        println(s2)
    }
}