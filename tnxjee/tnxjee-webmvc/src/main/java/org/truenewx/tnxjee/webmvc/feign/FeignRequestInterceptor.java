package org.truenewx.tnxjee.webmvc.feign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.api.RpcApi;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.service.feign.GrantAuthority;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.jwt.JwtGenerator;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign请求拦截器
 *
 * @author jianglei
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            Map<String, Collection<String>> feignHeaders = template.headers();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    // Feign头信息中未包含的才传递，以避免Feign创建的头信息被改动，且不传递JWT而是重新构建JWT
                    if (!feignHeaders.containsKey(headerName)
                            && !WebConstants.HEADER_RPC_JWT.equalsIgnoreCase(headerName)) {
                        Enumeration<String> requestHeaders = request.getHeaders(headerName);
                        Collection<String> headerValues = new ArrayList<>();
                        while (requestHeaders.hasMoreElements()) {
                            headerValues.add(requestHeaders.nextElement());
                        }
                        template.header(headerName, headerValues);
                    }
                }
            }
        }
        String jwt = generateJwt(template);
        if (jwt == null) { // 确保存在JWT头信息，以便于判断是否内部RPC
            jwt = Boolean.TRUE.toString();
        }
        if (jwt.length() > 8000) { // 单个头信息允许的最大长度为8192，超过8000则进行警告
            LogUtil.warn(getClass(), "====== The jwt length is {}.", jwt.length());
        } else {
            LogUtil.debug(getClass(), "====== The jwt length is {}.", jwt.length());
        }
        template.header(WebConstants.HEADER_RPC_JWT, jwt);

        // 确保远程调用始终使用JSON格式传递数据，避免出现html结果
        template.removeHeader(HttpHeaders.ACCEPT);
        template.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        template.header(WebConstants.HEADER_AJAX_REQUEST, WebConstants.AJAX_REQUEST_VALUE);
    }

    private String generateJwt(RequestTemplate template) {
        Class<?> apiClass = template.methodMetadata().method().getDeclaringClass();
        RpcApi apiAnno = AnnotationUtils.findAnnotation(apiClass, RpcApi.class);
        Assert.notNull(apiAnno, () -> "Annotation @" + RpcApi.class.getName() + " is required for " + apiClass);
        if (this.jwtGenerator.isAvailable()) {
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
                            // 不能将临时权限追加到会话级用户特性细节中，只能追加到用户特性细节克隆体中
                            userDetails = userDetails.clone();
                            addGrantedAuthority(userDetails, grantAuthority);
                        }
                        break;
                    default:
                        break;
                }
            }
            String type = apiAnno.value();
            if (StringUtils.isNotBlank(type)) {
                template.header(WebConstants.HEADER_RPC_TYPE, type);
            }
            return this.jwtGenerator.generate(type, userDetails);
        }
        return null;
    }

    private UserSpecificDetails<?> buildGrantUserSpecificDetails(GrantAuthority grantAuthority) {
        return SecurityUtil.buildDefaultUserDetails(grantAuthority.type(), grantAuthority.rank(), grantAuthority.app(),
                grantAuthority.permission());
    }

    private void addGrantedAuthority(UserSpecificDetails<?> userDetails, GrantAuthority grantAuthority) {
        SecurityUtil.addGrantedAuthority(userDetails, grantAuthority.type(), grantAuthority.rank(),
                grantAuthority.app(), grantAuthority.permission());
    }

}
