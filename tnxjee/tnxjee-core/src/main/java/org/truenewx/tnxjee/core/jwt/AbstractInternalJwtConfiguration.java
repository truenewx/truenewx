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
        return getRpcJwtExpiredTimeSeconds(this.profileSupplier.get());
    }

    /**
     * 获取RPC调用时JWT的过期时间秒数
     *
     * @param profile 运行环境
     * @return RPC调用时JWT的过期时间秒数
     */
    protected int getRpcJwtExpiredTimeSeconds(String profile) {
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
