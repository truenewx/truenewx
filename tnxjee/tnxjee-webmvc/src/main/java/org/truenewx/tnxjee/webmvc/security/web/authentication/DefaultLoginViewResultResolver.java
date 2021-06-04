package org.truenewx.tnxjee.webmvc.security.web.authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认的登录视图结果解决器
 */
public class DefaultLoginViewResultResolver implements LoginViewResultResolver {

    @Override
    public String resolveLoginViewResult(HttpServletRequest request) {
        return "/login";
    }

}
