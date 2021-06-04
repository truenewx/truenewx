package org.truenewx.tnxsample.cas.web.authentication;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjeex.cas.server.security.authentication.CasServerLoginViewResultResolver;
import org.truenewx.tnxsample.common.constant.AppNames;

@Component
public class LoginViewResultResolverImpl extends CasServerLoginViewResultResolver {

    @Override
    protected String getLoginViewResult(String appName) {
        if (AppNames.ADMIN.equals(appName)) {
            return "/login/manager";
        }
        return null;
    }

}
