package org.truenewx.tnxjeex.fss.service.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjeex.fss.service.own.OwnFssAccessor;

/**
 * 自有文件存储服务配置
 */
@Configuration
public class OwnFssServiceConfig {

    @Bean
    @ConditionalOnProperty("tnxjeex.fss.accessor.local.root")
    public OwnFssAccessor ownFssAccessor(FssLocalAccessorProperties properties) {
        return new OwnFssAccessor(properties.getRoot(), getOwnAccessorSalt());
    }

    protected Byte getOwnAccessorSalt() {
        return null;
    }

}
