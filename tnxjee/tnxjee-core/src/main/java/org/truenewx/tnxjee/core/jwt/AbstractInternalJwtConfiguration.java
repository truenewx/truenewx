package org.truenewx.tnxjee.core.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.util.Profiles;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;

/**
 * 抽象的内部JWT配置。
 */
public abstract class AbstractInternalJwtConfiguration implements InternalJwtConfiguration {

    @Autowired
    private ProfileSupplier profileSupplier;

    @Override
    public String getAppName() {
        return null;
    }

    @Override
    public int getExpiredIntervalSeconds() {
        return getExpiredIntervalSeconds(this.profileSupplier.get());
    }

    /**
     * 获取JWT过期时间秒数
     *
     * @param profile 运行环境
     * @return JWT的过期时间秒数
     */
    protected int getExpiredIntervalSeconds(String profile) {
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

}
