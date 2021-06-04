package org.truenewx.tnxjeex.cas.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.security.config.annotation.LoginSecurityConfigurerSupport;
import org.truenewx.tnxjee.webmvc.security.web.authentication.ResolvableExceptionAuthenticationFailureHandler;
import org.truenewx.tnxjeex.cas.client.web.CasClientLoginProcessingFilter;

/**
 * CAS客户端登录安全配置器
 */
@Component
public class CasClientLoginSecurityConfigurer
        extends LoginSecurityConfigurerSupport<CasClientLoginProcessingFilter, CasAuthenticationProvider> {

    @Autowired
    private ApiMetaProperties apiMetaProperties;
    @Autowired
    private RedirectStrategy redirectStrategy;
    @Autowired
    private ResolvableExceptionAuthenticationFailureHandler authenticationFailureHandler;

    @Override
    protected CasClientLoginProcessingFilter getProcessingFilter() {
        return new CasClientLoginProcessingFilter();
    }

    @Override
    protected void configure(HttpSecurity http, CasClientLoginProcessingFilter filter) {
        filter.setApplicationEventPublisher(getApplicationContext());
        filter.setRedirectStrategy(this.redirectStrategy);
        filter.acceptSuccessHandler(handler -> {
            handler.setTargetUrlParameter(this.apiMetaProperties.getLoginSuccessRedirectParameter());
        });
        filter.setAuthenticationFailureHandler(this.authenticationFailureHandler);
        http.addFilterAt(filter, CasAuthenticationFilter.class);
    }

}
