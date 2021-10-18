package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjee.webmvc.util.WebMvcUtil;

/**
 * WEB未登录访问限制的进入点
 */
public class WebAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired
    private RedirectStrategy redirectStrategy;
    @Autowired(required = false)
    private SecurityUrlProvider securityUrlProvider;
    @Autowired
    private ApiMetaProperties apiMetaProperties;
    private boolean ajaxGetToForm;

    public WebAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        if (this.securityUrlProvider != null) {
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
        return super.determineUrlToUseForThisRequest(request, response, exception);
    }

    /**
     * 设置是否将AJAX的GET请求直接跳转到登录表单页
     *
     * @param ajaxGetToForm 是否将AJAX的GET请求直接跳转到登录表单页
     */
    public void setAjaxGetToForm(boolean ajaxGetToForm) {
        this.ajaxGetToForm = ajaxGetToForm;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (WebMvcUtil.isInternalRpc(request)) { // 内部RPC调用直接返回401错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (WebUtil.isAjaxRequest(request)) { // AJAX请求执行特殊的跳转
            String redirectLoginUrl = buildRedirectUrlToLoginPage(request, response, authException);
            // AJAX POST请求无法通过自动登录重新提交，一定直接跳转到登录表单页，或者设置为AJAX的GET请求直接跳转到登录表单页
            if (this.ajaxGetToForm || HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
                response.setHeader(WebConstants.HEADER_LOGIN_URL, redirectLoginUrl);
            } else {
                this.redirectStrategy.sendRedirect(request, response, redirectLoginUrl);
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        super.commence(request, response, authException);
    }
}
