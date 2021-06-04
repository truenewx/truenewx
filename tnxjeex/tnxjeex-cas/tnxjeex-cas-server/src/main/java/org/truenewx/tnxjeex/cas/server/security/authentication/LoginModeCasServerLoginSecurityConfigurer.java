package org.truenewx.tnxjeex.cas.server.security.authentication;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.webmvc.security.web.authentication.LoginModeAuthenticationFilter;

/**
 * 支持多种登录方式的Cas服务端登录安全配置器
 */
@Component
public class LoginModeCasServerLoginSecurityConfigurer extends
        AbstractCasServerLoginSecurityConfigurer<LoginModeAuthenticationFilter, LoginModeCasServerAuthenticationProvider> {

    @Override
    protected LoginModeAuthenticationFilter getProcessingFilter() {
        return new LoginModeAuthenticationFilter(getApplicationContext());
    }

}
