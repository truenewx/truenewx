package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.jwt.InternalJwtResolver;
import org.truenewx.tnxjee.webmvc.security.authentication.UserSpecificDetailsAuthenticationToken;

/**
 * RPC JWT鉴定过滤器
 */
public class RpcJwtAuthenticationFilter extends GenericFilterBean {

    private InternalJwtResolver jwtResolver;

    public RpcJwtAuthenticationFilter(ApplicationContext context) {
        this.jwtResolver = context.getBean(InternalJwtResolver.class);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        boolean clearAuthentication = false;
        if (this.jwtResolver.isParsable()) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext != null) {
                HttpServletRequest request = (HttpServletRequest) req;
                String jwt = request.getHeader(WebConstants.HEADER_RPC_JWT);
                UserSpecificDetails<?> details = this.jwtResolver.parse(jwt, UserSpecificDetails.class);
                if (details != null) {
                    Authentication authResult = new UserSpecificDetailsAuthenticationToken(details);
                    securityContext.setAuthentication(authResult);
                    clearAuthentication = true; // 设置的一次性授权，需要在后续处理完之后清除
                }
            }
        }

        chain.doFilter(req, res);

        if (clearAuthentication) {
            SecurityContextHolder.clearContext();
        }
    }

}
