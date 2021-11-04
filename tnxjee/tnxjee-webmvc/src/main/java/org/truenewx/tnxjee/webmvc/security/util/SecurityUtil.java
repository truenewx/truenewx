package org.truenewx.tnxjee.webmvc.security.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.UserGrantedAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 安全工具类
 */
public class SecurityUtil {

    public static Function<Authentication, Object> GET_DETAIL_FUNCTION = Authentication::getDetails;
    public static BiConsumer<AbstractAuthenticationToken, Object> SET_DETAIL_CONSUMER = AbstractAuthenticationToken::setDetails;

    private SecurityUtil() {
    }

    public static void setAuthorizedUserDetails(UserSpecificDetails<?> userDetails) {
        Authentication authentication = getAuthentication();
        if (authentication instanceof AbstractAuthenticationToken) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) authentication;
            SET_DETAIL_CONSUMER.accept(token, userDetails);
        }
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
            return GET_DETAIL_FUNCTION.apply(authentication);
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
        Authentication authentication = getAuthentication();
        return authentication == null ? Collections.emptyList() : authentication.getAuthorities();
    }

    public static Collection<? extends GrantedAuthority> getGrantedAuthorities(String type, String app) {
        List<GrantedAuthority> filteredAuthorities = new ArrayList<>();
        Collection<? extends GrantedAuthority> grantedAuthorities = getGrantedAuthorities();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            if (grantedAuthority instanceof UserGrantedAuthority) {
                UserGrantedAuthority authority = (UserGrantedAuthority) grantedAuthority;
                if (authority.matches(type, null, app, null)) {
                    filteredAuthorities.add(authority);
                }
            }
        }
        return filteredAuthorities;
    }

    public static void setGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Authentication authentication = getAuthentication();
        if (authentication instanceof AbstractAuthenticationToken) {
            BeanUtil.setFieldValue(authentication, "authorities", Collections.unmodifiableCollection(authorities));
        }
    }

    public static boolean isAuthorized() {
        return getAuthorizedUserDetails() != null;
    }

}
