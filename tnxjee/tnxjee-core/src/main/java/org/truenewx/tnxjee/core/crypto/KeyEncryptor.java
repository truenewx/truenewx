package org.truenewx.tnxjee.core.crypto;

/**
 * 带密钥的加密器
 *
 * @author jianglei
 * 
 */
public interface KeyEncryptor {

    String encrypt(Object source, Object key);

}
