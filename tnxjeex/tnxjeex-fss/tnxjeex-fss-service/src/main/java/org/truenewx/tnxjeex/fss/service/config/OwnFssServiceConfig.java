package org.truenewx.tnxjeex.fss.service.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjeex.fss.service.own.EncryptOwnFssFileStreamProvider;
import org.truenewx.tnxjeex.fss.service.own.OwnFssAccessor;
import org.truenewx.tnxjeex.fss.service.own.OwnFssFileStreamProvider;

/**
 * 自有文件存储服务配置
 */
@Configuration
public class OwnFssServiceConfig {

    @Bean
    @ConditionalOnMissingBean(OwnFssFileStreamProvider.class)
    public OwnFssFileStreamProvider ownFssFileStreamProvider() {
        return new EncryptOwnFssFileStreamProvider();
    }

    @Bean
    @ConditionalOnProperty("tnxjeex.fss.accessor.local.root")
    public OwnFssAccessor ownFssAccessor(FssLocalAccessorProperties properties,
            OwnFssFileStreamProvider fileStreamProvider) {
        return new OwnFssAccessor(properties.getRoot(), fileStreamProvider);
    }

}
