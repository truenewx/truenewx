package org.truenewx.tnxjee.webmvc.servlet.mvc.method;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * 处理器方法处理器初始化器，负责将自定义的方法处理器加载到容器中
 */
@Component
public class HandlerMethodProcessorInitializer {

    private Class<?>[] argumentResolvers = { RequestResponseBodyMethodProcessor.class,
            RequestParamMethodArgumentResolver.class, RequestAttributeMethodArgumentResolver.class,
            PathVariableMethodArgumentResolver.class };
    private Class<?>[] returnValueHandlers = { RequestResponseBodyMethodProcessor.class };

    @Autowired
    public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter adapter) {
        List<HandlerMethodArgumentResolver> resolvers = adapter.getArgumentResolvers();
        if (resolvers != null) {
            List<HandlerMethodArgumentResolver> delegatedResolvers = new ArrayList<>();
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                if (ArrayUtils.contains(this.argumentResolvers, resolver.getClass())) {
                    delegatedResolvers.add(new EscapeHandlerMethodArgumentResolver(resolver));
                } else {
                    delegatedResolvers.add(resolver);
                }
            }
            adapter.setArgumentResolvers(delegatedResolvers);
        }

        List<HandlerMethodReturnValueHandler> handlers = adapter.getReturnValueHandlers();
        if (handlers != null) {
            List<HandlerMethodReturnValueHandler> delegatedHandlers = new ArrayList<>();
            for (HandlerMethodReturnValueHandler handler : handlers) {
                if (ArrayUtils.contains(this.returnValueHandlers, handler.getClass())) {
                    delegatedHandlers.add(new EscapeHandlerMethodReturnValueHandler(handler));
                } else {
                    delegatedHandlers.add(handler);
                }
            }
            adapter.setReturnValueHandlers(delegatedHandlers);
        }
    }

}
