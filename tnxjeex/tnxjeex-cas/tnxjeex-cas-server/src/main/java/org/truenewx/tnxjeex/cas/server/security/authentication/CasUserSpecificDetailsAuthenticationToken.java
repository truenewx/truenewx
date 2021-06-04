package org.truenewx.tnxjeex.cas.server.security.authentication;

import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.webmvc.security.authentication.UserSpecificDetailsAuthenticationToken;

/**
 * CAS用户特性细节鉴权令牌
 */
public class CasUserSpecificDetailsAuthenticationToken extends UserSpecificDetailsAuthenticationToken {

    private static final long serialVersionUID = -2997803056699252908L;

    private String service;
    private String scope;

    public CasUserSpecificDetailsAuthenticationToken(UserSpecificDetails<?> details, String service, String scope,
            String ip) {
        super(details);
        this.service = service;
        this.scope = scope;
        setIp(ip);
    }

    public String getService() {
        return this.service;
    }

    public String getScope() {
        return this.scope;
    }
}
