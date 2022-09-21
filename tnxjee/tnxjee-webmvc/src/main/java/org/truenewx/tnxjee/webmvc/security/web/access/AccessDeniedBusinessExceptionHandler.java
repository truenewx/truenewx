package org.truenewx.tnxjee.webmvc.security.web.access;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.exception.message.ResolvableExceptionMessageSaver;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;

/**
 * 访问拒绝后的业务异常处理器
 */
public class AccessDeniedBusinessExceptionHandler extends AccessDeniedHandlerImpl {

    @Autowired
    private ResolvableExceptionMessageSaver resolvableExceptionMessageSaver;
    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Throwable cause = accessDeniedException.getCause();
        if (cause instanceof ResolvableException) {
            HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
            this.resolvableExceptionMessageSaver.saveMessage(request, response, handlerMethod,
                    (ResolvableException) cause);
        }

        if (WebUtil.isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            super.handle(request, response, accessDeniedException);
        }
    }

}
