package org.truenewx.tnxjee.webmvc.cors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;

/**
 * 忽略空配置的Cors处理器
 *
 * @author jianglei
 */
public class IgnoreNullConfigCorsProcessor extends DefaultCorsProcessor {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    @Override
    public boolean processRequest(CorsConfiguration config, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (config == null) {
            HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
            if (handlerMethod == null) {
                return true;
            }
        }
        return super.processRequest(config, request, response);
    }

}
