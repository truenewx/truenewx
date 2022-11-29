package org.truenewx.tnxjee.webmvc.jwt;

/**
 * JWT对称解密方
 *
 * @author jianglei
 */
public abstract class JwtSymmetricDecryption extends JwtSymmetricSecretKeySupport implements JwtDecryption {

    @Override
    public final boolean isSymmetric(String type) {
        return true;
    }

    @Override
    public final String getDecryptSecretKey(String type, String payload) {
        return getSecretKey(type);
    }

}
