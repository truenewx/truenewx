package org.truenewx.tnxjee.core.crypto;

import org.truenewx.tnxjee.core.util.EncryptUtil;

/**
 * MD5加密器
 *
 * @author jianglei
 *
 */
public class Md5Encryptor implements Encryptor {

    @Override
    public String encrypt(Object source) {
        return EncryptUtil.encryptByMd5(source);
    }

}
