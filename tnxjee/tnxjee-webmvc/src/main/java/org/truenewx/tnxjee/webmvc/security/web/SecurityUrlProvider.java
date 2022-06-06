package org.truenewx.tnxjee.webmvc.security.web;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全相关地址提供者
 *
 * @author jianglei
 */
public interface SecurityUrlProvider {

    /**
     * 根据默认的登录表单地址
     *
     * @return 默认的登录表单地址
     */
    default String getDefaultLoginFormUrl() {
        return "/login";
    }

    /**
     * 根据请求获取登录表单地址
     *
     * @param request 请求
     * @return 登录表单地址
     */
    default String getLoginFormUrl(HttpServletRequest request) {
        return getDefaultLoginFormUrl();
    }

    /**
     * 获取登出处理地址
     *
     * @return 登出处理地址
     */
    default String getLogoutProcessUrl() {
        return "/logout";
    }

    /**
     * 获取登出成功后的跳转地址
     *
     * @return 登出成功后的跳转地址
     */
    default String getLogoutSuccessUrl() {
        return getDefaultLoginFormUrl();
    }

}
