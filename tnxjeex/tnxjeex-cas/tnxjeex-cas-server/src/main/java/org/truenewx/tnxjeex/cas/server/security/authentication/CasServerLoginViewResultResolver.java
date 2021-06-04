package org.truenewx.tnxjeex.cas.server.security.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.web.authentication.LoginViewResultResolver;
import org.truenewx.tnxjeex.cas.core.validation.constant.CasParameterNames;
import org.truenewx.tnxjeex.cas.server.service.CasServiceManager;

/**
 * Cas服务器登录视图结果解决器
 */
public abstract class CasServerLoginViewResultResolver implements LoginViewResultResolver {

    @Autowired
    private CasServiceManager serviceManager;

    @Override
    public String resolveLoginViewResult(HttpServletRequest request) {
        String service = WebUtil.getParameterOrAttribute(request, CasParameterNames.SERVICE);
        String appName = this.serviceManager.getAppName(service);
        return getLoginViewResult(appName);
    }

    protected abstract String getLoginViewResult(String appName);

}
