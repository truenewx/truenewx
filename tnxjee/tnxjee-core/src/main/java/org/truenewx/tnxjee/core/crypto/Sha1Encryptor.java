package org.truenewx.tnxjee.core.crypto;

import org.truenewx.tnxjee.core.util.EncryptUtil;

/**
 * SHA1加密器
 *
 * @author jianglei
 * 
 */
public class Sha1Encryptor implements Encryptor {

    @Override
    public String encrypt(Object source) {
        return EncryptUtil.encryptBySha1(source);
    }

}
