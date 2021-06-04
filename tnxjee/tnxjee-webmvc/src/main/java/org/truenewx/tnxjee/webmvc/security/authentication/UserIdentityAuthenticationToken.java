package org.truenewx.tnxjee.webmvc.security.authentication;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 用户标识的认证令牌
 */
public class UserIdentityAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = -5060938946411675544L;

    public UserIdentityAuthenticationToken(UserIdentity<?> userIdentity) {
        super(Collections.emptyList());
        super.setAuthenticated(true);
        setDetails(userIdentity);
    }

    @Override
    public Object getPrincipal() {
        return getDetails();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new UnsupportedOperationException();
    }

}
