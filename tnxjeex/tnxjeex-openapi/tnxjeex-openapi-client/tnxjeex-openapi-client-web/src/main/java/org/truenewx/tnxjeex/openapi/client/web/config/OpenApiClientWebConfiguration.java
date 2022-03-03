package org.truenewx.tnxjeex.openapi.client.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.webmvc.security.web.authentication.AjaxAuthenticationSuccessHandler;
import org.truenewx.tnxjeex.openapi.client.web.security.authentication.WechatAuthenticationSuccessHandler;

@Configuration
public class OpenApiClientWebConfiguration {

    @Bean
    @ConditionalOnMissingBean(AjaxAuthenticationSuccessHandler.class)
    public AjaxAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new WechatAuthenticationSuccessHandler();
    }

}
