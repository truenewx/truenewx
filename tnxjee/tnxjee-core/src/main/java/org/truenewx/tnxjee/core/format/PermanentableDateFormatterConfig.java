package org.truenewx.tnxjee.core.format;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermanentableDateFormatterConfig {

    @Bean
    @ConditionalOnMissingBean(PermanentableDateFormatter.class)
    public PermanentableDateFormatter permanentableDateFormatter() {
        return new PermanentableDateFormatter();
    }

}
