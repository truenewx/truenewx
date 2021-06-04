package org.truenewx.tnxsample.cas.web.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjeex.cas.server.security.authentication.CasServerLoginAuthenticator;
import org.truenewx.tnxsample.cas.web.rpc.CustomerLoginClient;
import org.truenewx.tnxsample.cas.web.rpc.ManagerLoginClient;
import org.truenewx.tnxsample.common.constant.AppNames;

@Component
public class UserLoginAuthenticator implements CasServerLoginAuthenticator<UsernamePasswordAuthenticationToken> {

    @Autowired
    private ManagerLoginClient managerLoginClient;
    @Autowired
    private CustomerLoginClient customerLoginClient;

    @Override
    public UserSpecificDetails<?> authenticate(String appName, String scope,
            UsernamePasswordAuthenticationToken token) {
        String username = (String) token.getPrincipal();
        String password = (String) token.getCredentials();
        switch (appName) {
            case AppNames.ADMIN:
                return this.managerLoginClient.validate(username, password);
            case AppNames.ROOT:
                return this.customerLoginClient.validate(username, password);
        }
        return null;
    }

}
