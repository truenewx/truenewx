package org.truenewx.tnxjee.webmvc.view.exception.resolver;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.TypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.truenewx.tnxjee.core.util.TemporalUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.exception.resolver.ResolvableExceptionResolver;
import org.truenewx.tnxjee.webmvc.function.WebContextPathPredicate;

/**
 * 视图层默认异常解决器
 */
public class ViewDefaultExceptionResolver extends DefaultHandlerExceptionResolver {

    private ViewErrorPathProperties pathProperties;
    private WebContextPathPredicate webContextPathPredicate;

    public ViewDefaultExceptionResolver(ViewErrorPathProperties pathProperties,
            WebContextPathPredicate webContextPathPredicate) {
        this.pathProperties = pathProperties;
        this.webContextPathPredicate = webContextPathPredicate;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        ModelAndView mav = super.doResolveException(request, response, handler, ex);
        if (mav == null) {
            // 非ajax请求且非可解决的异常，则跳转内部错误页
            if (!WebUtil.isAjaxRequest(request) && !ResolvableExceptionResolver.supports(ex)) {
                String path = this.pathProperties.getInternal();
                if (this.webContextPathPredicate.test(path)) {
                    mav = new ModelAndView(path);
                    mav.addObject("errorTime", TemporalUtil.format(LocalDateTime.now()));
                }
            }
        }
        return mav;
    }

    @Override
    protected ModelAndView handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request,
            HttpServletResponse response, Object handler) throws IOException {
        if (!WebUtil.isAjaxRequest(request)) { // 非ajax请求才跳转错误页面，否则采用默认处理
            String path = this.pathProperties.getBadRequest();
            if (this.webContextPathPredicate.test(path)) {
                return new ModelAndView(path, "exception", ex);
            }
        }
        return super.handleTypeMismatch(ex, request, response, handler);
    }

    @Override
    // 处理请求无对应控制器方法的异常，不能处理控制器方法抛出的404错误
    protected ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request,
            HttpServletResponse response, Object handler) throws IOException {
        if (!WebUtil.isAjaxRequest(request)) { // 非ajax请求才跳转错误页面，否则采用默认处理
            String path = this.pathProperties.getNotFound();
            if (this.webContextPathPredicate.test(path)) {
                return new ModelAndView(path);
            }
        }
        return super.handleNoHandlerFoundException(ex, request, response, handler);
    }

}
