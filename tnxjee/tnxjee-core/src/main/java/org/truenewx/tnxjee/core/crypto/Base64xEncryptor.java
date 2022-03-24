package org.truenewx.tnxjee.core.crypto;

import java.util.Random;

import org.truenewx.tnxjee.core.util.EncryptUtil;

/**
 * 含密钥的BASE64可逆算法加密器
 *
 * @author jianglei
 */
public class Base64xEncryptor implements KeyBidirectionalEncryptor {

    public final static Base64xEncryptor INSTANCE = new Base64xEncryptor();

    private Base64xEncryptor() {
    }

    @Override
    public String encrypt(Object source, Object key) {
        String random = String.valueOf(new Random().nextInt(32000));
        String encryptText = EncryptUtil.encryptByMd5(random);
        int j = 0;
        StringBuilder temp = new StringBuilder();
        char[] encryptedTextChars = encryptText.toCharArray();
        String text = source.toString();
        char[] textChars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            j = j == encryptedTextChars.length ? 0 : j;
            char c1 = textChars[i];
            char c2 = encryptedTextChars[j++];
            char c3 = (char) (c1 ^ c2);
            char c4 = encryptedTextChars[j - 1];
            temp.append(c4).append(c3);
        }
        return EncryptUtil.encryptByBase64(calculate(temp.toString(), key));
    }

    @Override
    public String decrypt(String encryptedText, Object key) {
        encryptedText = calculate(EncryptUtil.decryptByBase64(encryptedText), key);
        if (encryptedText == null) {
            return null;
        }
        StringBuilder text = new StringBuilder();
        char[] encryptedTextChar = encryptedText.toCharArray();
        for (int i = 0; i < encryptedText.length(); i++) {
            text.append((char) (encryptedTextChar[i] ^ encryptedTextChar[++i]));
        }
        return text.toString();
    }

    private static String calculate(String text, Object key) {
        if (text == null) {
            return null;
        }
        String keyString = EncryptUtil.encryptByMd5(key);
        int j = 0;
        StringBuilder temp = new StringBuilder();
        char[] encryptedKeyChars = keyString.toCharArray();
        char[] textChars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            j = j == keyString.length() ? 0 : j;
            temp.append((char) (textChars[i] ^ encryptedKeyChars[j++]));
        }
        return temp.toString();
    }
}
