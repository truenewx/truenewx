package org.truenewx.tnxjee.core.crypto;

import org.truenewx.tnxjee.core.util.EncryptUtil;

/**
 * BASE64可逆算法加密器
 *
 * @author jianglei
 */
public class Base64Encryptor implements BidirectionalEncryptor {

    public final static Base64Encryptor INSTANCE = new Base64Encryptor();

    private Base64Encryptor() {
    }

    @Override
    public String encrypt(Object source) {
        return EncryptUtil.encryptByBase64(source);
    }

    @Override
    public String decrypt(String encryptedText) {
        return EncryptUtil.decryptByBase64(encryptedText);
    }
}
