package org.truenewx.tnxjee.webmvc.security.access;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.caption.CaptionUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserGrantedAuthority;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.NoOperationAuthorityException;
import org.truenewx.tnxjee.service.security.access.GrantedAuthorityDecider;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;

/**
 * 基于用户权限的访问判定管理器
 */
public class UserAuthorityAccessDecisionManager extends UnanimousBased implements GrantedAuthorityDecider {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    public UserAuthorityAccessDecisionManager() {
        // 让父类判断方法校验Web表达式形式的权限
        super(Collections.singletonList(new WebExpressionVoter()));
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        super.decide(authentication, object, configAttributes);

        // 父类判断已通过，必然为已登录的用户
        FilterInvocation fi = (FilterInvocation) object;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (!contains(fi, authorities, configAttributes)) {
            HttpServletRequest request = fi.getRequest();
            HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
            String operation = CaptionUtil.getCaption(handlerMethod.getMethod(), request.getLocale());
            if (operation == null) {
                operation = WebUtil.getRelativeRequestAction(request);
            }
            BusinessException be = new NoOperationAuthorityException(operation);
            throw new AccessDeniedException(be.getLocalizedMessage(), be);
        }
    }

    private boolean contains(FilterInvocation fi, Collection<? extends GrantedAuthority> authorities,
            Collection<ConfigAttribute> configAttributes) {
        boolean granted = true; // 如果没有支持的配置属性限定，则授权默认为通过
        for (ConfigAttribute attribute : configAttributes) {
            if (supports(attribute)) {
                granted = false; // 只要有一个支持的配置属性限定，则授权默认为不通过
                if (contains(fi, authorities, (UserConfigAuthority) attribute)) {
                    return true; // 支持的用户配置权限限定有一个通过，则结果视为通过
                }
            }
        }
        return granted;
    }

    private boolean contains(FilterInvocation fi, Collection<? extends GrantedAuthority> authorities,
            UserConfigAuthority configAuthority) {
        if (configAuthority.isDenyAll()) {
            return false;
        }
        if (configAuthority.isIntranet()) { // 如果限制内网访问
            String ip = WebUtil.getRemoteAddress(fi.getHttpRequest());
            if (!NetUtil.isIntranetIp(ip)) {
                return false; // 拒绝非内网访问
            }
        }
        return isGranted(authorities, configAuthority.getType(), configAuthority.getRank(),
                configAuthority.getApp(), configAuthority.getPermission());
    }

    @Override
    public boolean isGranted(Collection<? extends GrantedAuthority> authorities, String type, String rank, String app,
            String permission) {
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority instanceof UserGrantedAuthority) {
                    UserGrantedAuthority userAuthority = (UserGrantedAuthority) authority;
                    if (userAuthority.matches(type, rank, app, permission)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof UserConfigAuthority;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
