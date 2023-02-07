package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.exception.message.ResolvableExceptionMessageSaver;
import org.truenewx.tnxjee.webmvc.servlet.mvc.WebMvcViewResolver;

/**
 * 基于可解决异常的登录认证失败处理器
 */
@Component
public class ResolvableExceptionAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired(required = false)
    private AuthenticationFailureViewResolver failureViewResolver = new DefaultAuthenticationFailureViewResolver();
    @Autowired
    private ResolvableExceptionMessageSaver resolvableExceptionMessageSaver;
    @Autowired
    private WebMvcViewResolver mvcViewResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        saveException(request, response, exception);

        // AJAX请求登录认证失败直接报401错误，不使用sendError()方法，以避免错误消息丢失
        if (WebUtil.isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String targetView = this.failureViewResolver.resolveFailureView(request);
            if (StringUtils.isBlank(targetView)) { // 登录认证失败后的目标地址未设置，则报401错误
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else { // 跳转到目标地址
                boolean useForward = true;
                String targetUrl = targetView;
                if (targetUrl.startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX)) {
                    targetUrl = targetUrl.substring(UrlBasedViewResolver.REDIRECT_URL_PREFIX.length());
                    useForward = false;
                }
                // 不是http地址又不是相对地址，则前面加/以视为相对地址
                if (!NetUtil.isHttpUrl(targetUrl, true) && !NetUtil.isRelativeUri(targetUrl)) {
                    targetUrl = Strings.SLASH + targetUrl;
                }
                if (useForward) {
                    this.mvcViewResolver.resolveView(request, response, targetUrl, null);
                } else {
                    response.sendRedirect(targetUrl);
                }
            }
        }
    }

    protected void saveException(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        Throwable cause = exception.getCause();
        // 最多支持两层异常原因
        if (!(cause instanceof ResolvableException)) {
            cause = cause.getCause();
        }
        if (cause instanceof ResolvableException) {
            ResolvableException re = (ResolvableException) cause;
            this.resolvableExceptionMessageSaver.saveMessage(request, response, null, re);
        }
    }

}
