package org.truenewx.tnxjee.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 系统属性配置器
 */
@Configuration
public class SystemPropertiesConfigurer {

    private static final String SYSTEM_PROPERTY_PREFIX = "system.";

    @Bean
    public EnvironmentPropertiesIterator systemPropertiesIterator() {
        // 将属性配置体系中system.开头的属性写到系统环境变量中
        return new EnvironmentPropertiesIterator((name, value) -> {
            if (name.startsWith(SYSTEM_PROPERTY_PREFIX) && value != null) {
                String key = name.substring(SYSTEM_PROPERTY_PREFIX.length());
                System.setProperty(key, value.toString());
            }
        });
    }

}
