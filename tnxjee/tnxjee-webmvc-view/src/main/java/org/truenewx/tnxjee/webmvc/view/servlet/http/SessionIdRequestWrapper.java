package org.truenewx.tnxjee.webmvc.view.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * 伪造sessionId的请求包装器
 *
 * @author jianglei
 */
public class SessionIdRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletResponse response;
    private String sessionCookieName = "JSESSIONID";
    private int sessionMaxAge = -1;

    public SessionIdRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
        super(request);
        this.response = response;
    }

    public SessionIdRequestWrapper setSessionCookieName(String sessionCookieName) {
        if (sessionCookieName != null) {
            this.sessionCookieName = sessionCookieName;
        }
        return this;
    }

    public SessionIdRequestWrapper setSessionMaxAge(Integer sessionMaxAge) {
        if (sessionMaxAge != null) {
            this.sessionMaxAge = sessionMaxAge;
        }
        return this;
    }

    @Override
    public HttpSession getSession() {
        HttpSession session = super.getSession();
        processSession(session);
        return session;
    }

    @Override
    public HttpSession getSession(boolean create) {
        HttpSession session = super.getSession(create);
        processSession(session);
        return session;
    }

    private void processSession(HttpSession session) {
        if (this.response == null || session == null) {
            return;
        }

        // cookieOverWritten - 用于过滤多个Set-Cookie头的标志
        Object cookieOverWritten = getAttribute("COOKIE_OVERWRITTEN_FLAG");
        if (cookieOverWritten == null && isSecure() && session.isNew()) {
            // 当是https协议，且新session时，创建JSESSIONID cookie以欺骗浏览器
            WebUtil.addCookie(this, this.response, this.sessionCookieName, session.getId(), this.sessionMaxAge);
            setAttribute("COOKIE_OVERWRITTEN_FLAG", "true"); // 过滤多个Set-Cookie头的标志
        }
    }
}
