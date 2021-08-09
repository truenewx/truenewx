package org.truenewx.tnxjeex.cas.client.web;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjeex.cas.core.constant.CasCookieNames;

/**
 * Cas客户端安全上下文准备过滤器
 */
public class CasClientSecurityContextPrepareFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String cookieTgt = WebUtil.getCookieValue(request, CasCookieNames.TGT);
        if (cookieTgt != null) {
            HttpSession session = request.getSession();
            String attributeName = getClass().getName() + Strings.WELL + CasCookieNames.TGT;
            String sessionTgt = (String) session.getAttribute(attributeName);
            if (sessionTgt == null) {
                session.setAttribute(attributeName, cookieTgt);
            } else if (!sessionTgt.equals(cookieTgt)) {
                // Cookie中的TGT与Session中的TGT不一致，出现在服务端已经重新登录，而客户端未被正确登出的情况下。
                // 此时重置客户端Session作为兜底，以避免客户端始终被错误地视为已登录却与服务端登录用户不同，而导致一系列错误
                session.invalidate();
                // Session失效后需重新创建才能赋值
                session = request.getSession();
                session.setAttribute(attributeName, cookieTgt);
            }
        }
        chain.doFilter(request, resp);
    }

}
