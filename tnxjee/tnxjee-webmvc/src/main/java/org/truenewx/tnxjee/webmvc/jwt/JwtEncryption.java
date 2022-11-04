package org.truenewx.tnxjee.webmvc.jwt;

import org.truenewx.tnxjee.core.spec.Named;

/**
 * JWT加密方
 *
 * @author jianglei
 */
public interface JwtEncryption extends Named {

    /**
     * 获取拼接在jwt串中的负载信息
     *
     * @param type 业务类型
     * @return 负载信息
     */
    String getPayload(String type);

    /**
     * 判断指定业务类型的密钥是否对称密钥
     *
     * @param type 业务类型
     * @return 是否对称密钥
     */
    boolean isSymmetric(String type);

    /**
     * 获取加密密钥
     *
     * @param type 业务类型
     * @return 加密密钥
     */
    String getEncryptSecretKey(String type);
}
