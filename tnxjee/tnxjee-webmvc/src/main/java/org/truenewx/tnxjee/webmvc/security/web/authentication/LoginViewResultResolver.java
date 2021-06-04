package org.truenewx.tnxjee.webmvc.security.web.authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录视图结果解决器
 */
public interface LoginViewResultResolver {

    String resolveLoginViewResult(HttpServletRequest request);

}
