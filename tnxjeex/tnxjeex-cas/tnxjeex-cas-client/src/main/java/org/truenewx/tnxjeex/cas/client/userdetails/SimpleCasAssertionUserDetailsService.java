package org.truenewx.tnxjeex.cas.client.userdetails;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.webmvc.security.core.BusinessAuthenticationException;
import org.truenewx.tnxjeex.cas.core.validation.SimpleAssertion;

/**
 * 简单的根据CasAssertion获取用户细节的服务
 */
@Component
public class SimpleCasAssertionUserDetailsService extends AbstractCasAssertionUserDetailsService {

    @Override
    protected UserDetails loadUserDetails(Assertion assertion) {
        if (assertion instanceof SimpleAssertion) {
            return ((SimpleAssertion) assertion).getUserDetails();
        }
        throw new BusinessAuthenticationException("error.service.security.authentication_failure");
    }

}
