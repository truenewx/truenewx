package org.truenewx.tnxjee.webmvc.servlet.mvc.method;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.truenewx.tnxjee.webmvc.http.HttpAction;

/**
 * 请求处理方法映射
 */
public interface HandlerMethodMapping {

    HandlerExecutionChain getHandlerChain(HttpServletRequest request);

    default HandlerMethod getHandlerMethod(HttpServletRequest request) {
        HandlerExecutionChain chain = getHandlerChain(request);
        if (chain != null) {
            Object handler = chain.getHandler();
            if (handler instanceof HandlerMethod) {
                return (HandlerMethod) handler;
            }
        }
        return null;
    }

    Map<HttpAction, HandlerMethod> getAllHandlerMethods();

    HandlerMethod getHandlerMethod(String uri, HttpMethod method);

}
