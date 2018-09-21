package me.principality.ktsql

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MainTest {
    @Test
    fun testFoo() {
        assertEquals(10, 10)
    }

    data class Jedi(val name: String, val age: Int)

    @Test
    fun testVertx() {
        val vertx = KotlinVertx()
        assertNotNull(vertx)
    }
}

