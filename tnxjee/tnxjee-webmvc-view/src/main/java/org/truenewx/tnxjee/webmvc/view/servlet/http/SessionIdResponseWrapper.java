package org.truenewx.tnxjee.webmvc.view.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

/**
 * 处理sessionId的响应包装器
 *
 * @author jianglei
 */
public class SessionIdResponseWrapper extends HttpServletResponseWrapper {

    public SessionIdResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
        super(response);
        if (request.isRequestedSessionIdFromURL()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
    }

}
