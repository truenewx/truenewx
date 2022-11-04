package org.truenewx.tnxjee.webmvc.jwt;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.crypto.Md5xEncryptor;

/**
 * JWT对称密钥支持
 *
 * @author jianglei
 */
public abstract class JwtSymmetricSecretKeySupport {

    private final Md5xEncryptor encryptor = new Md5xEncryptor(getStaticKey());
    private String defaultSecretKey; // 对称密钥大概率不区分业务类型，缓存默认密钥以提高密钥获取速度

    protected final String getSecretKey(String type, String encryptionName) {
        if (StringUtils.isBlank(type)) {
            if (this.defaultSecretKey == null) {
                this.defaultSecretKey = this.encryptor.encrypt(Strings.EMPTY, encryptionName);
            }
            return this.defaultSecretKey;
        }
        return this.encryptor.encrypt(type, encryptionName);
    }

    protected abstract long getStaticKey();

}
