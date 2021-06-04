package org.truenewx.tnxjeex.cas.client.web;

import java.util.function.Consumer;

import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.truenewx.tnxjee.core.util.BeanUtil;

/**
 * CAS客户端登录进程过滤器
 */
public class CasClientLoginProcessingFilter extends CasAuthenticationFilter {

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        if (redirectStrategy != null) {
            acceptSuccessHandler(handler -> handler.setRedirectStrategy(redirectStrategy));
            acceptFailureHandler(handler -> handler.setRedirectStrategy(redirectStrategy));
        }
    }

    public void acceptSuccessHandler(Consumer<AbstractAuthenticationTargetUrlRequestHandler> consumer) {
        AuthenticationSuccessHandler successHandler = getSuccessHandler();
        if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
            consumer.accept((AbstractAuthenticationTargetUrlRequestHandler) successHandler);
        }
    }

    public void acceptFailureHandler(Consumer<SimpleUrlAuthenticationFailureHandler> consumer) {
        AuthenticationFailureHandler failureHandler = getFailureHandler();
        if (!(failureHandler instanceof SimpleUrlAuthenticationFailureHandler)) {
            failureHandler = BeanUtil.getFieldValue(failureHandler, AuthenticationFailureHandler.class);
        }
        if (failureHandler instanceof SimpleUrlAuthenticationFailureHandler) {
            consumer.accept((SimpleUrlAuthenticationFailureHandler) failureHandler);
        }
    }

}
