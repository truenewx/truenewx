package org.truenewx.tnxjee.webmvc.exception.resolver;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.service.exception.*;

/**
 * 可解决异常处理至响应体中的解决器
 *
 * @author jianglei
 */
@Component
public class BodyResolvableExceptionResolver extends ResolvableExceptionResolver {

    public BodyResolvableExceptionResolver() {
        setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
    }

    @Override
    protected boolean supports(HttpServletRequest request, HandlerMethod handlerMethod) {
        return this.messageSaver.isResponseBody(request, handlerMethod);
    }

    @Override
    protected ModelAndView getResult(HttpServletRequest request, HttpServletResponse response,
            @Nullable HandlerMethod handlerMethod, ResolvableException re) {
        response.setStatus(getResponseStatus(re));
        return new ModelAndView();
    }

    private int getResponseStatus(ResolvableException re) {
        if (re instanceof BusinessException) {
            return HttpServletResponse.SC_FORBIDDEN;
        } else if (re instanceof FormatException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        } else if (re instanceof MultiException) {
            MultiException me = (MultiException) re;
            for (SingleException se : me) {
                return getResponseStatus(se); // 以第一个异常类型为准
            }
        }
        return HttpServletResponse.SC_OK;
    }

}
