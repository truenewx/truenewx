package org.truenewx.tnxjeex.cas.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjeex.cas.server.security.authentication.CasServerUserSpecificDetailsScopeSwitch;
import org.truenewx.tnxjeex.cas.server.security.authentication.logout.CasServerLogoutHandler;

/**
 * CAS服务端业务范围控制器支持。由实现子类指定访问路径，以更符合具体业务场景
 */
public abstract class CasServerScopeControllerSupport {

    @Autowired
    private CasServerLogoutHandler logoutHandler;
    @Autowired
    private CasServerUserSpecificDetailsScopeSwitch scopeSwitch;

    @ConfigAuthority
    public boolean scope(@RequestParam(value = "scope", required = false) String scope, HttpServletRequest request) {
        UserSpecificDetails<?> userDetails = SecurityUtil.getAuthorizedUserDetails();
        boolean switched = this.scopeSwitch.switchScope(userDetails, scope);
        if (switched) {
            this.logoutHandler.logoutClients(request);
        }
        return switched;
    }

}
