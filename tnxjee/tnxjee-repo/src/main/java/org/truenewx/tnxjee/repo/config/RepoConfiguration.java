package org.truenewx.tnxjee.repo.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.message.PropertiesMessageSource;
import org.truenewx.tnxjee.model.validation.config.ValidationConfigurationFactory;
import org.truenewx.tnxjee.repo.validation.config.DefaultValidationConfigurationFactory;

/**
 * 数据层配置
 *
 * @author jianglei
 */
@Configuration
public class RepoConfiguration {

    @Bean({ "messageSource", "messagesSource" })
    @Primary
    public MessageSource messageSource() {
        PropertiesMessageSource messageSource = new PropertiesMessageSource();
        messageSource.setBasenames("classpath:org/hibernate/validator/ValidationMessages",
                "classpath*:META-INF/message/constant/*", "classpath*:META-INF/message/error/*",
                "classpath*:META-INF/message/info/*", "classpath*:META-INF/region/*");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(Strings.ENCODING_UTF8);
        messageSource.setCacheSeconds(60);
        return messageSource;
    }

    @Bean("validator")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setValidationMessageSource(messageSource);
        return factoryBean;
    }

    @Bean
    @ConditionalOnMissingBean(ValidationConfigurationFactory.class)
    public ValidationConfigurationFactory validationConfigurationFactory() {
        return new DefaultValidationConfigurationFactory();
    }

}
