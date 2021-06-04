package org.truenewx.tnxjee.webmvc.security.util;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 安全工具类
 */
public class SecurityUtil {

    public static Function<Authentication, Object> DETAIL_FUNCTION = Authentication::getDetails;

    private SecurityUtil() {
    }

    /**
     * 获取已授权的当前用户细节，匿名用户将返回null
     *
     * @param <D> 用户特性细节类型
     * @return 已授权的当前用户细节
     */
    @SuppressWarnings("unchecked")
    public static <D extends UserSpecificDetails<?>> D getAuthorizedUserDetails() {
        Object details = getAuthenticationDetails();
        if (details instanceof UserSpecificDetails) {
            return (D) details;
        }
        return null;
    }

    private static Object getAuthenticationDetails() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            return DETAIL_FUNCTION.apply(authentication);
        }
        return null;
    }

    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            return context.getAuthentication();
        }
        return null;
    }


    /**
     * 获取已授权的当前用户标识，匿名用户将返回null
     *
     * @param <I> 用户标识类型
     * @return 已授权的当前用户标识
     */
    @SuppressWarnings("unchecked")
    public static <I extends UserIdentity<?>> I getAuthorizedUserIdentity() {
        Object details = getAuthenticationDetails();
        if (details instanceof UserSpecificDetails) {
            return (I) ((UserSpecificDetails<?>) details).getIdentity();
        } else if (details instanceof UserIdentity) {
            return (I) details;
        }
        return null;
    }

    public static Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        UserSpecificDetails<?> details = getAuthorizedUserDetails();
        return details == null ? Collections.emptyList() : details.getAuthorities();
    }

}
