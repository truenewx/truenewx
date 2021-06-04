package org.truenewx.tnxjee.webmvc.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.truenewx.tnxjee.core.util.ClassUtil;

/**
 * 抽象的认证提供者
 *
 * @param <A> 认证类型
 */
public abstract class AbstractAuthenticationProvider<A extends Authentication> implements AuthenticationProvider {

    @Override
    public boolean supports(Class<?> authentication) {
        Class<?> genericType = ClassUtil.getActualGenericType(getClass(), 0);
        return genericType != null && genericType.isAssignableFrom(authentication);
    }

}
