package org.truenewx.tnxjee.webmvc.servlet.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.web.util.WebConstants;

public class SameSiteCookieFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(req, resp);
        HttpServletResponse response = (HttpServletResponse) resp;
        String cookie = response.getHeader(WebConstants.HEADER_COOKIE);
        if (StringUtils.isNotBlank(cookie) && !cookie.contains("SameSite=")) {
            cookie += "; SameSite=None; Secure";
            response.setHeader(WebConstants.HEADER_COOKIE, cookie);
        }
    }

}
