package org.truenewx.tnxjee.core.crypto;

import java.util.Random;

import org.truenewx.tnxjee.core.util.EncryptUtil;

public class Base64xEncryptor implements KeyBidirectionalEncryptor {

    public final static Base64xEncryptor INSTANCE = new Base64xEncryptor();

    private Base64xEncryptor() {
    }

    @Override
    public String encrypt(Object source, Object key) {
        String random = String.valueOf(new Random().nextInt(32000));
        String encryptText = EncryptUtil.encryptByMd5(random);
        int j = 0;
        String temp = "";
        char encryptTextArray[] = encryptText.toCharArray();
        String text = source.toString();
        char textChar[] = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            j = j == encryptTextArray.length ? 0 : j;
            char c1 = textChar[i];
            char c2 = encryptTextArray[j++];
            char c3 = (char) (c1 ^ c2);
            char c4 = encryptTextArray[j - 1];
            temp += c4 + "" + c3;
        }
        return EncryptUtil.encryptByBase64(calculate(temp, key));
    }

    @Override
    public String decrypt(String encryptedText, Object key) {
        encryptedText = calculate(EncryptUtil.decryptByBase64(encryptedText), key);
        if (encryptedText == null) {
            return null;
        }
        String text = "";
        char encryptedTextChar[] = encryptedText.toCharArray();
        for (int i = 0; i < encryptedText.length(); i++) {
            text += (char) (encryptedTextChar[i] ^ encryptedTextChar[++i]);
        }
        return text;
    }

    private static String calculate(String text, Object key) {
        if (text == null) {
            return null;
        }
        String keyString = EncryptUtil.encryptByMd5(key);
        int j = 0;
        String temp = "";
        char encryptKeyChar[] = keyString.toCharArray();
        char textChar[] = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            j = j == keyString.length() ? 0 : j;
            char c = (char) (textChar[i] ^ encryptKeyChar[j++]);
            temp = temp + c;
        }
        return temp;
    }
}
