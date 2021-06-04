package org.truenewx.tnxjeex.openapi.client.web.security.authentication;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.core.BusinessAuthenticationException;
import org.truenewx.tnxjee.webmvc.security.web.authentication.AjaxAuthenticationSuccessHandler;
import org.truenewx.tnxjee.webmvc.security.web.authentication.ResolvableExceptionAuthenticationFailureHandler;

/**
 * 微信登录认证过滤器
 *
 * @author jianglei
 */
public class WechatLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String REQUEST_URL_PREFIX = "/login/";
    private static final String REQUEST_URL_PATTERN = REQUEST_URL_PREFIX + Strings.ASTERISK; // 所有/login/*请求

    private Map<String, WechatAuthenticationTokenResolver> tokenResolverMapping = new HashMap<>();

    public static RequestMatcher getRequestMatcher(boolean onlyPost) {
        String method = onlyPost ? HttpMethod.POST.name() : null;
        return new AntPathRequestMatcher(REQUEST_URL_PATTERN, method);
    }

    public WechatLoginAuthenticationFilter(ApplicationContext context, boolean onlyPost) {
        super(getRequestMatcher(onlyPost));

        context.getBeansOfType(WechatAuthenticationTokenResolver.class).forEach((id, resolver) -> {
            String loginMode = resolver.getLoginMode();
            if (StringUtils.isNotBlank(loginMode)) {
                this.tokenResolverMapping.put(loginMode, resolver);
            }
        });

        AjaxAuthenticationSuccessHandler successHandler = SpringUtil
                .getFirstBeanByClass(context, AjaxAuthenticationSuccessHandler.class);
        if (successHandler != null) {
            setAuthenticationSuccessHandler(successHandler);
        }

        ResolvableExceptionAuthenticationFailureHandler failureHandler = SpringUtil
                .getFirstBeanByClass(context, ResolvableExceptionAuthenticationFailureHandler.class);
        if (failureHandler != null) {
            setAuthenticationFailureHandler(failureHandler); // 指定登录失败时的处理器
        }
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!super.requiresAuthentication(request, response)) {
            return false;
        }
        return getTokenResolver(request) != null;
    }

    private WechatAuthenticationTokenResolver getTokenResolver(HttpServletRequest request) {
        String action = WebUtil.getRelativeRequestAction(request);
        if (action.startsWith(REQUEST_URL_PREFIX)) {
            String loginMode = action.substring(REQUEST_URL_PREFIX.length());
            int index = loginMode.indexOf(Strings.SLASH);
            if (index > 0) {
                loginMode = loginMode.substring(0, index);
            }
            return this.tokenResolverMapping.get(loginMode);
        }
        return null;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        WechatAuthenticationTokenResolver tokenResolver = getTokenResolver(request);
        try {
            WechatAuthenticationToken authRequest = tokenResolver.resolveAuthenticationToken(request);
            setDetails(request, authRequest);
            return getAuthenticationManager().authenticate(authRequest);
        } catch (BusinessException e) {
            throw new BusinessAuthenticationException(e);
        }
    }

    protected void setDetails(HttpServletRequest request, WechatAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

}
