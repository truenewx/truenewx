package org.truenewx.tnxjee.model.spec.user.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.model.spec.user.UserSpecific;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 用户特性细节
 *
 * @param <I> 用户标识类型
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface UserSpecificDetails<I extends UserIdentity<?>> extends UserSpecific<I>, UserDetails {

    UserSpecificDetails<I> clone();

}
