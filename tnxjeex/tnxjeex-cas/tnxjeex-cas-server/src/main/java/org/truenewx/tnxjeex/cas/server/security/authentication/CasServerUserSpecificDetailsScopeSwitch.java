package org.truenewx.tnxjeex.cas.server.security.authentication;

import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * Cas服务端用户特性细节范围切换器
 */
public interface CasServerUserSpecificDetailsScopeSwitch {

    boolean switchScope(UserSpecificDetails<?> specificDetails, String scope);

}
