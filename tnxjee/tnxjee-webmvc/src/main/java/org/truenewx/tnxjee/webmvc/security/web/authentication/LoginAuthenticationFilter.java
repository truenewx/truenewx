package org.truenewx.tnxjee.webmvc.security.web.authentication;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;

/**
 * 登录认证过滤器
 */
public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public LoginAuthenticationFilter(ApplicationContext context) {
        ApiMetaProperties apiMetaProperties = SpringUtil.getFirstBeanByClass(context, ApiMetaProperties.class);
        if (apiMetaProperties != null) {
            String successTargetUrlParameter = apiMetaProperties.getLoginSuccessRedirectParameter();
            if (StringUtils.isNotBlank(successTargetUrlParameter)) {
                AuthenticationSuccessHandler successHandler = getSuccessHandler();
                if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
                    ((AbstractAuthenticationTargetUrlRequestHandler) successHandler)
                            .setTargetUrlParameter(successTargetUrlParameter);
                }
            }
        }
        ResolvableExceptionAuthenticationFailureHandler failureHandler = SpringUtil
                .getFirstBeanByClass(context, ResolvableExceptionAuthenticationFailureHandler.class);
        if (failureHandler != null) {
            setAuthenticationFailureHandler(failureHandler); // 指定登录失败时的处理器
        }
    }

    @Override
    public AuthenticationSuccessHandler getSuccessHandler() {
        return super.getSuccessHandler();
    }

    @Override
    public AuthenticationFailureHandler getFailureHandler() {
        return super.getFailureHandler();
    }

}
