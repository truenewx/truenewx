package org.truenewx.tnxjee.webmvc.exception.resolver;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * 处理运行期异常至响应体中的解决器
 */
@Component
public class BodyRuntimeExceptionResolver extends AbstractHandlerExceptionResolver {

    public BodyRuntimeExceptionResolver() {
        setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        // 处理运行时异常中不是可解决异常的那部分异常
        if (ex instanceof RuntimeException && !(ex instanceof ResolvableException)) {
            // 只处理ajax请求
            if (WebUtil.isAjaxRequest(request)) {
                LogUtil.error(getClass(), ex);
                try {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    String message = Objects.requireNonNullElse(ex.getCause(), ex).getMessage();
                    response.getWriter().print(message);
                } catch (IOException ignored) {
                }
                return new ModelAndView();
            }
        }
        return null;
    }

}
