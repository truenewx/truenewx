package org.truenewx.tnxjee.core.crypto;

/**
 * 带密钥的解密器
 *
 * @author jianglei
 * 
 */
public interface KeyDecryptor {

    String decrypt(String encryptedText, Object key);

}
