package org.truenewx.tnxjee.webmvc.jwt;

/**
 * JWT解密方
 *
 * @author jianglei
 */
public interface JwtDecryption {

    /**
     * 获取对应的加密方名称
     *
     * @return 加密方名称
     */
    String getEncryptionName();

    /**
     * 判断指定业务类型的密钥是否对称密钥
     *
     * @param type 业务类型
     * @return 是否对称密钥
     */
    boolean isSymmetric(String type);

    /**
     * 获取解密密钥
     *
     * @param type    业务类型
     * @param payload 负载
     * @return 解密密钥
     */
    String getDecryptSecretKey(String type, String payload);
}
