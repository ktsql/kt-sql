package me.principality.ktsql.protocol.mysql

import me.principality.ktsql.protocol.mysql.helper.AuthPluginData
import me.principality.ktsql.utils.config.MySQLEncrpyt
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Test
import java.sql.DriverManager
import kotlin.experimental.xor

class MySQLTest {
    @Test
    fun testProtocol() {
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:30000/hbase","user","")
        val statement = conn.createStatement()
        val result = statement.executeQuery("select * from user")
        while (result.next()) {
            println(result.getString(""))
        }
        statement.close()
        conn.close()
    }

    @Test
    fun testSha1() {
        val sha1Password = DigestUtils.sha1("pass")
        println(sha1Password)

        println(MySQLEncrpyt.sha1("pass"))
    }

    @Test
    fun testEncrpyt() {
        val authPluginData = AuthPluginData()
        val bytes = getAuthCipherBytes("pass", authPluginData)
        println(bytes)
        println(MySQLEncrpyt.getAuthCipherBytes("pass", authPluginData))
        //println(DigestUtils.sha1("pass"))
    }

    private fun getAuthCipherBytes(password: String, authPluginData: AuthPluginData): ByteArray {
        val sha1Password = DigestUtils.sha1(password)
        val doubleSha1Password = DigestUtils.sha1(sha1Password)
        val concatBytes = ByteArray(authPluginData.getAuthPluginData().size + doubleSha1Password.size)
        System.arraycopy(authPluginData.getAuthPluginData(), 0, concatBytes, 0, authPluginData.getAuthPluginData().size)
        System.arraycopy(doubleSha1Password, 0, concatBytes, authPluginData.getAuthPluginData().size, doubleSha1Password.size)
        val sha1ConcatBytes = DigestUtils.sha1(concatBytes)
        println(sha1Password)
        println(sha1ConcatBytes)
        return xor(sha1Password, sha1ConcatBytes)
    }

    private fun xor(input: ByteArray, secret: ByteArray): ByteArray {
        val result = ByteArray(input.size)
        for (i in input.indices) {
            result[i] = (input[i] xor secret[i]).toByte()
        }
        return result
    }
}