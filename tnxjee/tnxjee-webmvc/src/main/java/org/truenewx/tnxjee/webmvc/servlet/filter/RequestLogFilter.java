package org.truenewx.tnxjee.webmvc.servlet.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.servlet.http.BodyRepeatableRequestWrapper;

/**
 * 请求日志打印过滤器
 */
public class RequestLogFilter implements Filter {

    private String[] urlPatterns;

    private Logger logger = LogUtil.getLogger(getClass());

    public RequestLogFilter(String... urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        if (ArrayUtils.isNotEmpty(this.urlPatterns)) {
            HttpServletRequest request = (HttpServletRequest) req;
            String url = WebUtil.getRelativeRequestUrl(request);
            if (StringUtil.antPathMatchOneOf(url, this.urlPatterns)) {
                request = new BodyRepeatableRequestWrapper(request);
                this.logger.info("====== request from {} ======", WebUtil.getRemoteAddress(request));
                this.logger.info("{} {}", request.getMethod(), url);
                this.logger.info("headers: {}", JsonUtil.toJson(WebUtil.getHeaders(request)));
                this.logger.info("parameters: {}", JsonUtil.toJson(WebUtil.getRequestParameterMap(request)));
                this.logger.info("body: {}", JsonUtil.toJson(WebUtil.getRequestBodyMap(request)));
                req = request;
            }
        }
        chain.doFilter(req, resp);
    }

}
