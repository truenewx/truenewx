package org.truenewx.tnxjee.core.config;

import org.apache.commons.lang3.StringUtils;

/**
 * 内部JWT配置。当且仅当内部微服务间RPC调用使用了@GrantAuthority注解指定授予权限时，才构建当前类的实例作为Spring的Bean。
 */
public class InternalJwtConfiguration {

    private String secretKey;
    private int expiredIntervalSeconds;

    /**
     * @param secretKey              密钥
     * @param expiredIntervalSeconds 过期间隔秒数，应大于服务的启动时间
     */
    public InternalJwtConfiguration(String secretKey, int expiredIntervalSeconds) {
        this.secretKey = secretKey;
        this.expiredIntervalSeconds = expiredIntervalSeconds;
    }

    public InternalJwtConfiguration(String secretKey) {
        this(secretKey, 120);
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public int getExpiredIntervalSeconds() {
        return this.expiredIntervalSeconds;
    }

    public boolean isValid() {
        return this.expiredIntervalSeconds > 0 && StringUtils.isNotBlank(this.secretKey);
    }

}
