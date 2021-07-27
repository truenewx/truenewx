package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.truenewx.tnxjee.core.config.InternalJwtConfiguration;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.core.util.JacksonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.webmvc.security.authentication.UserSpecificDetailsAuthenticationToken;
import org.truenewx.tnxjee.webmvc.util.RpcUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * 内部JWT鉴定过滤器
 */
public class InternalJwtAuthenticationFilter extends GenericFilterBean {

    private JWTVerifier verifier;

    public InternalJwtAuthenticationFilter(ApplicationContext context) {
        InternalJwtConfiguration configuration = SpringUtil
                .getFirstBeanByClass(context, InternalJwtConfiguration.class);
        if (configuration != null && configuration.isValid()) {
            String secretKey = configuration.getSecretKey();
            this.verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        boolean clearAuthentication = false;
        if (this.verifier != null) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext != null) {
                String token = RpcUtil.getInternalJwt((HttpServletRequest) req);
                if (token != null) {
                    try {
                        DecodedJWT jwt = this.verifier.verify(token);
                        String audienceJson = CollectionUtil.getFirst(jwt.getAudience(), null);
                        if (StringUtils.isNotBlank(audienceJson)) {
                            UserSpecificDetails<?> details = (UserSpecificDetails<?>) JacksonUtil.CLASSED_MAPPER
                                    .readValue(audienceJson, UserSpecificDetails.class);
                            Authentication authResult = new UserSpecificDetailsAuthenticationToken(details);
                            securityContext.setAuthentication(authResult);
                            clearAuthentication = true; // 设置的一次性授权，需要在后续处理完之后清除
                        }
                    } catch (Exception e) { // 出现任何错误均只打印日志，视为没有授权
                        LogUtil.error(getClass(), e);
                    }
                }
            }
        }

        chain.doFilter(req, res);

        if (clearAuthentication) {
            SecurityContextHolder.clearContext();
        }
    }

}
