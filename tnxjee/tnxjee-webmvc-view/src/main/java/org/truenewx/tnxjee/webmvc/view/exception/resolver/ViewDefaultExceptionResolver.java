package org.truenewx.tnxjee.webmvc.view.exception.resolver;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
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
            if (!ResolvableExceptionResolver.supports(ex)) {
                String path = this.pathProperties.getInternal();
                if (this.webContextPathPredicate.test(path)) {
                    return new ModelAndView(path);
                }
            }
        }
        return mav;
    }

    @Override
    protected ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request,
            HttpServletResponse response, Object handler) throws IOException {
        String path = this.pathProperties.getNotFound();
        if (this.webContextPathPredicate.test(path)) {
            return new ModelAndView(path);
        }
        return super.handleNoHandlerFoundException(ex, request, response, handler);
    }

}
