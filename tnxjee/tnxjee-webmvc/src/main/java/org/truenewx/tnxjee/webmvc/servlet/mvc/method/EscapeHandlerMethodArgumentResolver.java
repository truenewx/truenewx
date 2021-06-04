package org.truenewx.tnxjee.webmvc.servlet.mvc.method;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 转义方法参数解决器
 */
public class EscapeHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private HandlerMethodArgumentResolver delegate;

    public EscapeHandlerMethodArgumentResolver(HandlerMethodArgumentResolver delegate) {
        Assert.notNull(delegate, "The delegate must be not null");
        this.delegate = delegate;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return this.delegate.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = this.delegate.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (MethodParameterEscapeProcessor.INSTANCE.supports(parameter)) {
            arg = MethodParameterEscapeProcessor.INSTANCE.escape(arg, false);
        }
        return arg;
    }

}
