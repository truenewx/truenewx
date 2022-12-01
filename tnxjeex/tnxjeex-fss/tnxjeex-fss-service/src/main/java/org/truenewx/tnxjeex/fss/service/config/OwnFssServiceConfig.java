package org.truenewx.tnxjeex.fss.service.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjeex.fss.service.storage.own.EncryptOwnFssFileStreamProvider;
import org.truenewx.tnxjeex.fss.service.storage.own.OwnFssFileStreamProvider;
import org.truenewx.tnxjeex.fss.service.storage.own.OwnFssStorageAccessor;

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
    @ConditionalOnProperty(FssLocalAccessorProperties.PROPERTIES_PREFIX + ".root")
    @ConditionalOnExpression("T(org.apache.commons.lang3.StringUtils).isNotBlank('${" +
            FssLocalAccessorProperties.PROPERTIES_PREFIX + ".root}')")
    public OwnFssStorageAccessor ownFssAccessor(FssLocalAccessorProperties properties,
            OwnFssFileStreamProvider fileStreamProvider) {
        return new OwnFssStorageAccessor(properties.getRoot(), fileStreamProvider);
    }

}
