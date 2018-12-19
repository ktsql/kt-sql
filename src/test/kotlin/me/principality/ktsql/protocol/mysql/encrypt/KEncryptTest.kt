package me.principality.ktsql.protocol.mysql.encrypt

import me.principality.ktsql.protocol.mysql.helper.AuthPluginData
import me.principality.ktsql.utils.MySQLEncrpyt
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Test
import kotlin.experimental.and
import kotlin.experimental.xor

/**
 * 对sha1进行测试，检验java在调用不同的库时，是否产生一样的byte array
 */
class KEncryptTest {
    @Test
    fun testSha1() {
        val sha1Pass1 = DigestUtils.sha1("pass")
        val sha1Pass2 = MySQLEncrpyt.sha1("pass")

        for (i in sha1Pass1.indices) {
            if (sha1Pass1[i] != sha1Pass2[i]) {
                println("error: ${i}")
                break
            }
        }

        println(byteArrayToHexString(sha1Pass1))
        println(byteArrayToHexString(sha1Pass2))
    }

    fun byteArrayToHexString(b: ByteArray): String {
        var result = ""
        for (i in b.indices) {
            result += Integer.toString((b[i] and 0xff.toByte()) + 0x100, 16).substring(1)
        }
        return result
    }

    @Test
    fun testEncrpyt() {
        val authPluginData = AuthPluginData()
        val bytes = getAuthCipherBytes("pass", authPluginData)
        println(byteArrayToHexString(bytes))
        println(byteArrayToHexString(MySQLEncrpyt.getAuthCipherBytes("pass", authPluginData)))
    }

    private fun getAuthCipherBytes(password: String, authPluginData: AuthPluginData): ByteArray {
        val sha1Password = DigestUtils.sha1(password)
        val doubleSha1Password = DigestUtils.sha1(sha1Password)
        val concatBytes = ByteArray(authPluginData.getAuthPluginData().size + doubleSha1Password.size)
        System.arraycopy(authPluginData.getAuthPluginData(), 0, concatBytes, 0, authPluginData.getAuthPluginData().size)
        System.arraycopy(doubleSha1Password, 0, concatBytes, authPluginData.getAuthPluginData().size, doubleSha1Password.size)
        val sha1ConcatBytes = DigestUtils.sha1(concatBytes)
        println(byteArrayToHexString(sha1Password))
        println(byteArrayToHexString(sha1ConcatBytes))
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