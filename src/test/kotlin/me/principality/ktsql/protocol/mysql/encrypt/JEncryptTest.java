package me.principality.ktsql.protocol.mysql.encrypt;

import me.principality.ktsql.protocol.mysql.helper.AuthPluginData;
import me.principality.ktsql.utils.MySQLEncrpyt;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * 对sha1进行测试，检验java在调用不同的库时，是否产生一样的byte array
 *
 * 结论：同样的sha1函数，在.java中调用和.kt中调用，产生出不同的byte array，
 * 这导致了某些代码只能在.java中运行
 */
public class JEncryptTest {
    @Test
    public void testSha1() {
        byte[] sha1Pass1 = DigestUtils.sha1("pass");
        byte[] sha1Pass2 = MySQLEncrpyt.sha1("pass");

        for (int i = 0; i < sha1Pass1.length; i++) {
            if (sha1Pass1[i] != sha1Pass2[i]) {
                System.out.println(String.format("error: %d", i));
                break;
            }
        }
        System.out.println(byteArrayToHexString(sha1Pass1));
        System.out.println(byteArrayToHexString(sha1Pass2));
    }

    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    @Test
    public void testEncrypt() {
        final AuthPluginData authPluginData = new AuthPluginData();
        byte[] bytes = getAuthCipherBytes("pass", authPluginData);
        System.out.println(byteArrayToHexString(bytes));
        System.out.println(byteArrayToHexString(MySQLEncrpyt.getAuthCipherBytes("pass", authPluginData)));
    }

    private byte[] getAuthCipherBytes(final String password, final AuthPluginData authPluginData) {
        byte[] sha1Password = DigestUtils.sha1(password);
        byte[] doubleSha1Password = DigestUtils.sha1(sha1Password);
        byte[] concatBytes = new byte[authPluginData.getAuthPluginData().length + doubleSha1Password.length];
        System.arraycopy(authPluginData.getAuthPluginData(), 0, concatBytes, 0, authPluginData.getAuthPluginData().length);
        System.arraycopy(doubleSha1Password, 0, concatBytes, authPluginData.getAuthPluginData().length, doubleSha1Password.length);
        byte[] sha1ConcatBytes = DigestUtils.sha1(concatBytes);
        System.out.println(byteArrayToHexString(sha1Password));
        System.out.println(byteArrayToHexString(sha1ConcatBytes));
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
