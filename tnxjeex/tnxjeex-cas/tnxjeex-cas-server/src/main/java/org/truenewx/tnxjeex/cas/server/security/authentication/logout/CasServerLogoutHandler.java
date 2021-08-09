package org.truenewx.tnxjeex.cas.server.security.authentication.logout;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * CAS服务端登出处理器
 */
public interface CasServerLogoutHandler extends LogoutHandler {

    /**
     * 登出当前所有已登录客户端应用
     *
     * @param request HTTP请求
     */
    void logoutClients(HttpServletRequest request);

}
