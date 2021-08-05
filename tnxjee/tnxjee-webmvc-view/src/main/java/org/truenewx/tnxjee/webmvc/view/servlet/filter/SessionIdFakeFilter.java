package org.truenewx.tnxjee.webmvc.view.servlet.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.webmvc.view.servlet.http.SessionIdRequestWrapper;
import org.truenewx.tnxjee.webmvc.view.servlet.http.SessionIdResponseWrapper;

/**
 * 伪造SessionId的过滤器，以解决http和https之间切换时sessionId丢失的问题，当一个站点同时存在http和https链接时用到
 */
public class SessionIdFakeFilter implements Filter {

    private String sessionCookieName;
    private Integer sessionMaxAge;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.sessionCookieName = filterConfig.getInitParameter("sessionCookieName");
        this.sessionMaxAge = MathUtil.parseInteger(filterConfig.getInitParameter("sessionMaxAge"));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        request = new SessionIdRequestWrapper(request, response)
                .setSessionCookieName(this.sessionCookieName)
                .setSessionMaxAge(this.sessionMaxAge);
        response = new SessionIdResponseWrapper(request, response);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
