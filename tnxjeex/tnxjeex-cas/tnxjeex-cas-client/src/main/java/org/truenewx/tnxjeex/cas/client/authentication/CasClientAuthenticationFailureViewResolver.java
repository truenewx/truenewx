package org.truenewx.tnxjeex.cas.client.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.webmvc.security.web.authentication.AuthenticationFailureViewResolver;
import org.truenewx.tnxjeex.cas.client.config.CasClientProperties;

/**
 * CAS客户端的登录认证失败的视图解决器
 *
 * @author jianglei
 */
// 对于同时作为CAS服务端和客户端的应用不可注册为Bean，只有单纯作为CAS客户端的应用才可以注册为Bean
public class CasClientAuthenticationFailureViewResolver implements AuthenticationFailureViewResolver {

    @Autowired
    private CasClientProperties casClientProperties;

    @Override
    public String resolveFailureView(HttpServletRequest request) {
        // 客户端登录失败，服务端不一定失败，可能登录成功，此时需执行服务端登出操作，才能避免因服务端登录成功而导致反复重定向
        request.setAttribute("loginFormUrl", this.casClientProperties.getLogoutSuccessUrl());
        return getFailureViewName();
    }

    protected String getFailureViewName() {
        return "/error/login";
    }

}
