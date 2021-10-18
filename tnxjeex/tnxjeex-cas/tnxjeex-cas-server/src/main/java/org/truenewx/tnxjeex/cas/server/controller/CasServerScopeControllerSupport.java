package org.truenewx.tnxjeex.cas.server.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjee.webmvc.security.web.AjaxRedirectStrategy;
import org.truenewx.tnxjeex.cas.server.security.authentication.CasServerScopeApplicator;
import org.truenewx.tnxjeex.cas.server.security.authentication.logout.CasServerLogoutHandler;

/**
 * CAS服务端业务范围控制器支持。由实现子类指定访问路径，以更符合具体业务场景
 */
public abstract class CasServerScopeControllerSupport {

    @Autowired
    private CasServerLogoutHandler logoutHandler;
    @Autowired
    private CasServerScopeApplicator scopeApplicator;
    @Autowired
    private AjaxRedirectStrategy redirectStrategy;

    @RequestMapping("/scope")
    @ConfigAuthority
    public void scope(@RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = WebConstants.DEFAULT_LOGIN_SUCCESS_REDIRECT_PARAMETER, required = false) String next,
            HttpServletRequest request, HttpServletResponse response) {
        UserSpecificDetails<?> userDetails = SecurityUtil.getAuthorizedUserDetails();
        if (this.scopeApplicator.applyScope(userDetails, scope)) {
            this.logoutHandler.logoutClients(request);
        }
        if (StringUtils.isNotBlank(next)) {
            try {
                this.redirectStrategy.sendRedirect(request, response, next);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
