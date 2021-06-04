package org.truenewx.tnxjee.webmvc.feign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.config.InternalJwtConfiguration;
import org.truenewx.tnxjee.model.spec.user.DefaultUserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.DefaultUserSpecificDetails;
import org.truenewx.tnxjee.model.spec.user.security.UserGrantedAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.service.feign.GrantAuthority;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjee.webmvc.util.RpcUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 请求头信息传递拦截器
 *
 * @author jianglei
 */
@Component
public class RequestHeaderDeliverInterceptor implements RequestInterceptor {

    @Autowired(required = false)
    private InternalJwtConfiguration internalJwtConfiguration;

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = SpringWebContext.getRequest();
        boolean noJwt = true;
        if (request != null) {
            Map<String, Collection<String>> feignHeaders = template.headers();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Feign头信息中未包含的才传递，以避免Feign创建的头信息被改动
                if (!feignHeaders.containsKey(headerName)) {
                    Enumeration<String> requestHeaders = request.getHeaders(headerName);
                    Collection<String> headerValues = new ArrayList<>();
                    while (requestHeaders.hasMoreElements()) {
                        headerValues.add(requestHeaders.nextElement());
                    }
                    template.header(headerName, headerValues);
                    if (noJwt && WebConstants.HEADER_INTERNAL_JWT.equals(headerName)) {
                        noJwt = false;
                    }
                }
            }
        }
        if (noJwt) { // 没有JWT则构建JWT传递
            String token = generateJwt(template);
            if (token == null) { // 确保存在JWT头信息，以便于判断是否内部RPC
                token = Boolean.TRUE.toString();
            }
            template.header(WebConstants.HEADER_INTERNAL_JWT, token);
        }
    }

    private String generateJwt(RequestTemplate template) {
        if (this.internalJwtConfiguration != null && this.internalJwtConfiguration.isValid()) {
            UserSpecificDetails<?> userDetails = SecurityUtil.getAuthorizedUserDetails();
            Class<?> targetType = template.feignTarget().type();
            GrantAuthority grantAuthority = targetType.getAnnotation(GrantAuthority.class);
            if (grantAuthority != null) {
                GrantAuthority.Mode mode = grantAuthority.mode();
                switch (mode) {
                    case UNAUTHORIZED:
                        if (userDetails == null) {
                            userDetails = buildGrantUserSpecificDetails(grantAuthority);
                        }
                        break;
                    case REPLACEMENT:
                        userDetails = buildGrantUserSpecificDetails(grantAuthority);
                        break;
                    case ADDON:
                        if (userDetails == null) {
                            userDetails = buildGrantUserSpecificDetails(grantAuthority);
                        } else {
                            addGrantedAuthorities(userDetails, grantAuthority);
                        }
                        break;
                    default:
                        break;
                }
            }
            return RpcUtil.generateInternalJwt(userDetails, this.internalJwtConfiguration.getSecretKey(),
                    this.internalJwtConfiguration.getExpiredIntervalSeconds());
        }
        return null;
    }

    private UserSpecificDetails<?> buildGrantUserSpecificDetails(GrantAuthority grantAuthority) {
        DefaultUserSpecificDetails userDetails = new DefaultUserSpecificDetails();
        addGrantedAuthorities(userDetails, grantAuthority);
        DefaultUserIdentity identity = new DefaultUserIdentity(grantAuthority.type(), 0);
        userDetails.setIdentity(identity);
        userDetails.setUsername(identity.getId().toString());
        userDetails.setCaption(identity.toString());
        userDetails.setEnabled(true);
        userDetails.setAccountNonExpired(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(true);
        return userDetails;
    }

    @SuppressWarnings("unchecked")
    private void addGrantedAuthorities(UserSpecificDetails<?> userDetails, GrantAuthority grantAuthority) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) userDetails.getAuthorities();
        UserGrantedAuthority userAuthority = new UserGrantedAuthority(grantAuthority.type(), grantAuthority.rank(),
                grantAuthority.app());
        userAuthority.addPermissions(grantAuthority.permission());
        authorities.add(userAuthority);
    }

}
