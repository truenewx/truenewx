package org.truenewx.tnxjeex.cas.client.web.servlet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.cors.SingleCorsConfigurationSource;

/**
 * CAS客户端登录处理器映射
 */
public class CasClientLoginHandlerMapping implements HandlerMapping {

    private String processUrl;
    @Autowired
    private SingleCorsConfigurationSource corsConfigurationSource;

    public CasClientLoginHandlerMapping(String processUrl) {
        this.processUrl = processUrl;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (WebUtil.getRelativeRequestUrl(request).equals(this.processUrl)) {
            return new HandlerExecutionChain(this.corsConfigurationSource);
        }
        return null;
    }

}
