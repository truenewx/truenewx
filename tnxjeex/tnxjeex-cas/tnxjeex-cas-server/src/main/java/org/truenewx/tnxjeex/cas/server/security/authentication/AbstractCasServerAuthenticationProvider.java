package org.truenewx.tnxjeex.cas.server.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.webmvc.security.authentication.AbstractAuthenticationProvider;
import org.truenewx.tnxjeex.cas.server.service.CasServerExceptionCodes;
import org.truenewx.tnxjeex.cas.server.service.CasServiceManager;

/**
 * 抽象的Cas服务端认证提供者
 *
 * @param <A> 认证类型
 */
public abstract class AbstractCasServerAuthenticationProvider<A extends Authentication>
        extends AbstractAuthenticationProvider<A> {

    @Autowired
    private CasServiceManager serviceManager;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        @SuppressWarnings("unchecked")
        A token = (A) authentication;
        Object details = token.getDetails();
        if (details instanceof CasServiceAuthenticationDetails) {
            CasServiceAuthenticationDetails authenticationDetails = (CasServiceAuthenticationDetails) details;
            String service = authenticationDetails.getService();
            String appName = this.serviceManager.getAppName(service);
            String scope = authenticationDetails.getScope();
            String ip = authenticationDetails.getIp();
            try {
                UserSpecificDetails<?> userDetails = authenticate(appName, scope, token);
                if (userDetails == null) {
                    throw new BusinessException(CasServerExceptionCodes.UNSUPPORTED_AUTHENTICATE_MODE);
                }
                return new CasUserSpecificDetailsAuthenticationToken(userDetails, service, scope, ip);
            } catch (BusinessException e) {
                throw new BadCredentialsException(e.getLocalizedMessage(), e);
            }
        }
        return null;
    }

    protected abstract UserSpecificDetails<?> authenticate(String appName, String scope, A token);
}
