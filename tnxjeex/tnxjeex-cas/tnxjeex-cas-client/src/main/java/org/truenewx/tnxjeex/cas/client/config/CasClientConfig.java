package org.truenewx.tnxjeex.cas.client.config;

import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjeex.cas.client.userdetails.SimpleCasAssertionUserDetailsService;
import org.truenewx.tnxjeex.cas.client.validation.CasJsonServiceTicketValidator;
import org.truenewx.tnxjeex.cas.client.web.servlet.CasClientLoginHandlerMapping;

@Configuration
public class CasClientConfig {

    static {
        SecurityUtil.DETAIL_FUNCTION = authentication -> {
            if (authentication instanceof CasAuthenticationToken) {
                return ((CasAuthenticationToken) authentication).getUserDetails();
            }
            return authentication.getDetails();
        };
    }

    @Autowired
    private CasClientProperties properties;

    @Bean
    public CasClientLoginHandlerMapping casClientLoginHandlerMapping() {
        return new CasClientLoginHandlerMapping("/login/cas"); // 使用CasAuthenticationFilter的默认登录处理地址
    }

    @Bean
    public TicketValidator ticketValidator() {
        return new CasJsonServiceTicketValidator(this.properties.getServerContextUri(true));
    }

    @Bean
    public CasAuthenticationProvider authenticationProvider(
            SimpleCasAssertionUserDetailsService assertionUserDetailsService) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setAuthenticationUserDetailsService(assertionUserDetailsService);
        provider.setTicketValidator(ticketValidator());
        provider.setKey(this.properties.getService());
        provider.setServiceProperties(this.properties);
        return provider;
    }

}
