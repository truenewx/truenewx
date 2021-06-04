package org.truenewx.tnxjee.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.truenewx.tnxjee.core.util.Profiles;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;

/**
 * 核心配置支持
 */
public abstract class CoreConfigurerSupport {

    @Autowired
    private ProfileSupplier profileSupplier;

    @Bean
    public InternalJwtConfiguration internalJwtConfiguration() {
        String profile = this.profileSupplier.get();
        int expiredTimeSeconds = getInternalJwtExpiredTimeSeconds(profile);
        return new InternalJwtConfiguration(getInternalJwtSecretKey(), expiredTimeSeconds);
    }

    /**
     * 获取指定运行环境中的内部JWT过期时间秒数
     *
     * @param profile 运行环境
     * @return 内部JWT过期时间秒数
     */
    protected int getInternalJwtExpiredTimeSeconds(String profile) {
        switch (profile) {
            case Profiles.LOCAL:
            case Profiles.DEV:
                return 1000;
            case Profiles.TEST:
                return 100;
            default:
                return 10;
        }
    }

    /**
     * @return 内部JWT密钥
     */
    protected abstract String getInternalJwtSecretKey();

}
