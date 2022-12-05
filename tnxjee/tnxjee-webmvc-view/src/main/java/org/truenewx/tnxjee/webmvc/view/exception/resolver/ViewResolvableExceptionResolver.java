package org.truenewx.tnxjee.webmvc.view.exception.resolver;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.service.exception.*;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.exception.resolver.ResolvableExceptionResolver;
import org.truenewx.tnxjee.webmvc.view.exception.annotation.ResolvableExceptionResult;
import org.truenewx.tnxjee.webmvc.view.util.WebViewUtil;

/**
 * 可解决异常处理至视图页面的解决器
 *
 * @author jianglei
 */
@Component
public class ViewResolvableExceptionResolver extends ResolvableExceptionResolver {

    @Autowired
    private ViewErrorPathProperties pathProperties;

    public String getBusinessErrorPath() {
        return this.pathProperties.getBusiness();
    }

    public ViewResolvableExceptionResolver() {
        setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
    }

    @Override
    protected boolean supports(HttpServletRequest request, HandlerMethod handlerMethod) {
        return !this.messageSaver.isResponseBody(request, handlerMethod);
    }

    private String getErrorPath(ResolvableException re) {
        if (re instanceof BusinessException) {
            return getBusinessErrorPath();
        } else if (re instanceof FormatException) {
            return this.pathProperties.getFormat();
        } else if (re instanceof MultiException) {
            MultiException me = (MultiException) re;
            for (SingleException se : me) {
                return getErrorPath(se); // 以第一个异常类型为准
            }
        }
        return null;
    }

    @Override
    protected ModelAndView getResult(HttpServletRequest request, HttpServletResponse response,
            @Nullable HandlerMethod handlerMethod, ResolvableException re) {
        String errorPath = getErrorPath(re);
        ModelAndView mav = new ModelAndView(errorPath);
        mav.addObject("ajaxRequest", WebUtil.isAjaxRequest(request));
        ResolvableExceptionResult rer = null;
        if (handlerMethod != null) {
            rer = handlerMethod.getMethodAnnotation(ResolvableExceptionResult.class);
        }
        if (rer != null) {
            String view = rer.value();
            if (ResolvableExceptionResult.PREV_VIEW.equals(view)) {
                view = WebViewUtil.getRelativePreviousUrl(request, false);
            }
            if (StringUtils.isEmpty(view)) { // 跳转到全局错误页面，则需设置返回按钮地址
                mav.addObject("back", rer.back());
            } else { // 非跳转到全局错误页面，则复制参数到属性集中，以便于可能的回填
                mav.setViewName(view);
                WebUtil.copyParameters2Attributes(request);
            }
        }
        return mav;
    }

}
