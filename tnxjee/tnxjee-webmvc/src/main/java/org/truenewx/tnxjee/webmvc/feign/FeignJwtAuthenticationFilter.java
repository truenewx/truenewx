package org.truenewx.tnxjee.webmvc.feign;

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
import org.truenewx.tnxjee.webmvc.jwt.JwtParser;
import org.truenewx.tnxjee.webmvc.security.authentication.UserSpecificDetailsAuthenticationToken;

/**
 * Feign JWT鉴定过滤器
 */
public class FeignJwtAuthenticationFilter extends GenericFilterBean {

    private JwtParser jwtParser;

    public FeignJwtAuthenticationFilter(ApplicationContext context) {
        this.jwtParser = context.getBean(JwtParser.class);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        boolean clearAuthentication = false;
        if (this.jwtParser.isAvailable()) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext != null) {
                HttpServletRequest request = (HttpServletRequest) req;
                String type = request.getHeader(WebConstants.HEADER_RPC_TYPE);
                String jwt = request.getHeader(WebConstants.HEADER_RPC_JWT);
                UserSpecificDetails<?> details = this.jwtParser.parse(type, jwt, UserSpecificDetails.class);
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
