package me.principality.ktsql.protocol.mysql;

import me.principality.ktsql.protocol.mysql.helper.AuthPluginData;
import me.principality.ktsql.utils.config.MySQLEncrpyt;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class MySQLEncryptTest {
    @Test
    public void testSha1() {
        byte[] sha1Password = DigestUtils.sha1("pass");
        System.out.println(sha1Password);

        System.out.println(MySQLEncrpyt.sha1("pass"));
    }

    @Test
    public void testEncrypt() {
        final AuthPluginData authPluginData = new AuthPluginData();
        byte[] bytes = getAuthCipherBytes("pass", authPluginData);
        System.out.println(bytes);
        System.out.println(MySQLEncrpyt.getAuthCipherBytes("pass", authPluginData));
//        System.out.println(DigestUtils.sha1("pass"));
    }

    private byte[] getAuthCipherBytes(final String password, final AuthPluginData authPluginData) {
        byte[] sha1Password = DigestUtils.sha1(password);
        byte[] doubleSha1Password = DigestUtils.sha1(sha1Password);
        byte[] concatBytes = new byte[authPluginData.getAuthPluginData().length + doubleSha1Password.length];
        System.arraycopy(authPluginData.getAuthPluginData(), 0, concatBytes, 0, authPluginData.getAuthPluginData().length);
        System.arraycopy(doubleSha1Password, 0, concatBytes, authPluginData.getAuthPluginData().length, doubleSha1Password.length);
        byte[] sha1ConcatBytes = DigestUtils.sha1(concatBytes);
        System.out.println(sha1Password);
        System.out.println(sha1ConcatBytes);
        return xor(sha1Password, sha1ConcatBytes);
    }

    private byte[] xor(final byte[] input, final byte[] secret) {
        final byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; ++i) {
            result[i] = (byte) (input[i] ^ secret[i]);
        }
        return result;
    }
}
