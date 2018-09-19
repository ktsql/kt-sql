package me.principality.sqlexec

import org.junit.Test
import kotlin.test.assertEquals

class CalciteTest {
    @Test
    fun testSqlPacketHandler() {
        val handler = SqlPacketHandler()
        val r = handler.execute("select * from table")
        assertEquals(0, 0)
    }
}