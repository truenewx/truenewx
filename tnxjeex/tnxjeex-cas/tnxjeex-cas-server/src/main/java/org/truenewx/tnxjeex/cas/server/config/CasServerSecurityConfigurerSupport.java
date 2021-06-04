package org.truenewx.tnxjeex.cas.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.truenewx.tnxjee.webmvc.security.config.annotation.web.configuration.WebHttpSecurityConfigurer;
import org.truenewx.tnxjee.webmvc.view.security.config.WebViewSecurityConfigurerSupport;
import org.truenewx.tnxjeex.cas.server.security.authentication.CasAuthenticationSuccessHandler;
import org.truenewx.tnxjeex.cas.server.security.authentication.logout.CasServerLogoutHandler;
import org.truenewx.tnxjeex.cas.server.security.authentication.logout.CasServerLogoutSuccessHandler;

/**
 * CAS服务端安全配置器支持
 */
public class CasServerSecurityConfigurerSupport extends WebViewSecurityConfigurerSupport {

    @Autowired
    private CasServerLogoutHandler logoutHandler;

    @Bean
    public CasAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CasAuthenticationSuccessHandler();
    }

    @Bean
    public WebHttpSecurityConfigurer logoutConfigurer() {
        return http -> {
            http.logout().addLogoutHandler(CasServerSecurityConfigurerSupport.this.logoutHandler);
        };
    }

    @Bean
    @Override
    public LogoutSuccessHandler logoutSuccessHandler() {
        CasServerLogoutSuccessHandler handler = new CasServerLogoutSuccessHandler();
        handler.setDefaultTargetUrl(this.urlProvider.getLogoutSuccessUrl());
        return handler;
    }

}
