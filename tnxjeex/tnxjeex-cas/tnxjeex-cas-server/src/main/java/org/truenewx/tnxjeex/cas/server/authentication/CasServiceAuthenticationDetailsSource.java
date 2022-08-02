package org.truenewx.tnxjeex.cas.server.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjeex.cas.core.CasConstants;

/**
 * CasServiceAuthenticationDetails源
 */
public class CasServiceAuthenticationDetailsSource implements
        AuthenticationDetailsSource<HttpServletRequest, CasServiceAuthenticationDetails> {

    @Override
    public CasServiceAuthenticationDetails buildDetails(HttpServletRequest request) {
        String service = WebUtil.getParameterOrAttribute(request, CasConstants.PARAMETER_SERVICE);
        String scope = WebUtil.getParameterOrAttribute(request, CasConstants.PARAMETER_SCOPE);
        String ip = WebUtil.getRemoteAddress(request);
        return new CasServiceAuthenticationDetails(service, scope, ip);
    }

}
