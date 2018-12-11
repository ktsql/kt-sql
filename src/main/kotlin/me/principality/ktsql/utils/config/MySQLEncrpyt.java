package me.principality.ktsql.utils.config;

import me.principality.ktsql.protocol.mysql.helper.AuthPluginData;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * MySQL密码加密方法
 * https://dev.mysql.com/doc/internals/en/secure-password-authentication.html#packet-Authentication::Native41
 */
public class MySQLEncrpyt {
    public static byte[] getAuthCipherBytes(final String password, final AuthPluginData authPluginData) {
        byte[] sha1Password = DigestUtils.sha1(password);
        byte[] doubleSha1Password = DigestUtils.sha1(sha1Password);
        byte[] concatBytes = new byte[authPluginData.getAuthPluginData().length + doubleSha1Password.length];
        System.arraycopy(authPluginData.getAuthPluginData(), 0, concatBytes, 0, authPluginData.getAuthPluginData().length);
        System.arraycopy(doubleSha1Password, 0, concatBytes, authPluginData.getAuthPluginData().length, doubleSha1Password.length);
        byte[] sha1ConcatBytes = DigestUtils.sha1(concatBytes);
        return xor(sha1Password, sha1ConcatBytes);
    }

    private static byte[] xor(final byte[] input, final byte[] secret) {
        final byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; ++i) {
            result[i] = (byte) (input[i] ^ secret[i]);
        }
        return result;
    }

    public static byte[] sha1(String s) {
        return DigestUtils.sha1(s);
    }
}
