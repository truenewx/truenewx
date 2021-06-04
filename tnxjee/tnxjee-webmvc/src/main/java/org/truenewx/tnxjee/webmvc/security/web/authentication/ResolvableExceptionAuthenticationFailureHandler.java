package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.exception.message.ResolvableExceptionMessageSaver;

/**
 * 基于可解决异常的登录认证失败处理器
 */
@Component
public class ResolvableExceptionAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private boolean useForward = true;
    private LoginViewResultResolver loginViewResultResolver = new DefaultLoginViewResultResolver();
    @Autowired
    private ResolvableExceptionMessageSaver resolvableExceptionMessageSaver;
    @Autowired
    private WebMvcProperties webMvcProperties;

    public void setUseForward(boolean useForward) {
        this.useForward = useForward;
    }

    @Autowired(required = false)
    public void setLoginViewResultResolver(LoginViewResultResolver loginViewResultResolver) {
        this.loginViewResultResolver = loginViewResultResolver;
    }

    public LoginViewResultResolver getLoginViewResultResolver() {
        return this.loginViewResultResolver;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        saveException(request, response, exception);

        // AJAX请求登录认证失败直接报401错误，不使用sendError()方法，以避免错误消息丢失
        if (WebUtil.isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String targetUrl = this.loginViewResultResolver.resolveLoginViewResult(request);
            if (StringUtils.isBlank(targetUrl)) { // 登录认证失败后的跳转地址未设置，也报401错误
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else { // 跳转到目标地址
                if (this.useForward) {
                    WebMvcProperties.View view = this.webMvcProperties.getView();
                    targetUrl = view.getPrefix() + targetUrl + view.getSuffix();
                    request.getRequestDispatcher(targetUrl).forward(request, response);
                } else {
                    response.sendRedirect(targetUrl);
                }
            }
        }
    }

    protected void saveException(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof ResolvableException) {
            ResolvableException re = (ResolvableException) cause;
            this.resolvableExceptionMessageSaver.saveMessage(request, response, null, re);
        }
    }

}
