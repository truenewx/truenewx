package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjee.webmvc.util.WebMvcUtil;

/**
 * WEB未登录访问限制的进入点
 */
public class WebAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private SecurityUrlProvider securityUrlProvider;
    @Autowired
    private RedirectStrategy redirectStrategy;
    @Autowired
    private ApiMetaProperties apiMetaProperties;

    public WebAuthenticationEntryPoint(@NotNull SecurityUrlProvider securityUrlProvider) {
        super(securityUrlProvider.getDefaultLoginFormUrl());
        this.securityUrlProvider = securityUrlProvider;
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        String loginFormUrl = this.securityUrlProvider.getLoginFormUrl(request);
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            String nextUrl = request.getRequestURI() + Strings.QUESTION + queryString;
            String redirectParameter = this.apiMetaProperties.getLoginSuccessRedirectParameter();
            loginFormUrl += Strings.AND + redirectParameter + Strings.EQUAL
                    + URLEncoder.encode(nextUrl, StandardCharsets.UTF_8);
        }
        return loginFormUrl;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (WebMvcUtil.isInternalRpc(request)) { // 内部RPC调用直接返回401错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (WebUtil.isAjaxRequest(request)) { // AJAX请求执行特殊的跳转
            String loginPageUrl = buildRedirectUrlToLoginPage(request, response, authException);
            // AJAX POST请求无法通过自动登录重新提交，或者默认登录页面地址是相对地址（在当前应用，无需转发试探），则直接跳转到登录页面
            if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())
                    || NetUtil.isRelativeUrl(getLoginFormUrl())) {
                response.setHeader(WebConstants.HEADER_LOGIN_URL, loginPageUrl);
            } else {
                this.redirectStrategy.sendRedirect(request, response, loginPageUrl);
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        super.commence(request, response, authException);
    }

}
