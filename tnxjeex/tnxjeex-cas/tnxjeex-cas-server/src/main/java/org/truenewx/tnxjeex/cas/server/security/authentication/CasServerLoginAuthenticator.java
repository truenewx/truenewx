package org.truenewx.tnxjeex.cas.server.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * Cas服务端登录认证器
 *
 * @param <T> 令牌类型
 */
public interface CasServerLoginAuthenticator<T extends AbstractAuthenticationToken> {

    default Class<T> getTokenType() {
        return ClassUtil.getActualGenericType(getClass(), CasServerLoginAuthenticator.class, 0);
    }

    UserSpecificDetails<?> authenticate(String appName, String scope, T token);

}
