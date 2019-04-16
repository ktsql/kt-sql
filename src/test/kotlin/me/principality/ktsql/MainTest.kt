package me.principality.ktsql

import me.principality.ktsql.utils.KotlinVertx
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Demo Test boilerplate
 */
class MainTest {
    @Test
    fun testFoo() {
        assertEquals(10, 10)
    }

    @Test
    fun testVertx() {
        val vertx = KotlinVertx()
        assertNotNull(vertx)
    }
}

