package me.principality.ktsql.protocol.mysql.helper

import com.google.common.base.Strings
import me.principality.ktsql.utils.config.ConfigureProvider
import org.apache.commons.codec.digest.DigestUtils
import kotlin.experimental.xor

class AuthorityHelper {
    val authPluginData: AuthPluginData = AuthPluginData()

    fun login(username: String, authResponse: ByteArray): Boolean {
        val proxyAuthority = ConfigureProvider.getLoginAuthority()
        return if (Strings.isNullOrEmpty(proxyAuthority.password)) {
            proxyAuthority.username.equals(username)
        } else {
            proxyAuthority.username.equals(username) && MySQLEncrpyt.isPassEqual(proxyAuthority.password, authResponse, authPluginData)
        }
    }

    private fun getAuthCipherBytes(password: String): ByteArray {
        val sha1Password = DigestUtils.sha1(password)
        val doubleSha1Password = DigestUtils.sha1(sha1Password)
        val concatBytes = ByteArray(authPluginData.getAuthPluginData().size + doubleSha1Password.size)
        System.arraycopy(authPluginData.getAuthPluginData(), 0, concatBytes, 0, authPluginData.getAuthPluginData().size)
        System.arraycopy(doubleSha1Password, 0, concatBytes, authPluginData.getAuthPluginData().size, doubleSha1Password.size)
        val sha1ConcatBytes = DigestUtils.sha1(concatBytes)
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