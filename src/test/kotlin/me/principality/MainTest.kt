package me.principality

import kotlinx.coroutines.experimental.launch
import org.junit.Test
import kotlin.test.*
import org.litote.kmongo.*
import org.litote.kmongo.async.KMongo
import org.litote.kmongo.coroutine.findOne
import org.litote.kmongo.coroutine.getCollection
import org.litote.kmongo.coroutine.insertOne

class MainTest {
    @Test
    fun testFoo() {
        assertEquals(10, 10)
    }

    @Test
    fun testAbci() {
        val counter = KotlinCounter()
        assertNotNull(counter)
    }

    data class Jedi(val name: String, val age: Int)

    @Test
    fun testMongo() {
        val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
        val database = client.getDatabase("test") //normal java driver usage
        val collection = database.getCollection<Jedi>() //KMongo extension method

        //here the name of the collection by convention is "jedi"
        //you can use getCollection<Jedi>("otherjedi") if the collection name is different
        launch {
            collection.insertOne(Jedi("Luke Skywalker", 19))
            val luke: Jedi? = collection.findOne(Jedi::name eq "Luke Skywalker")
            assertNotNull(luke)
        }
    }

    @Test
    fun testVertx() {
        val vertx = KotlinVertx()
        assertNotNull(vertx)
    }
}

