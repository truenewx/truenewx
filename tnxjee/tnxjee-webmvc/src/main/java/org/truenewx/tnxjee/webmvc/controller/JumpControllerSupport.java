package org.truenewx.tnxjee.webmvc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;

/**
 * 跳转控制器支持
 */
public abstract class JumpControllerSupport {

    @RequestMapping("/http/**")
    @ConfigAnonymous
    public void http(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) Map<String, Object> body) throws Exception {
        String targetUrl = getTargetUrl(NetUtil.PROTOCOL_HTTP, request);
        jump(request, response, targetUrl, body);
    }

    @RequestMapping("/https/**")
    @ConfigAnonymous
    public void https(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) Map<String, Object> body) throws Exception {
        String targetUrl = getTargetUrl(NetUtil.PROTOCOL_HTTPS, request);
        jump(request, response, targetUrl, body);
    }

    @RequestMapping("/to/**")
    @ConfigAnonymous
    public void to(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) Map<String, Object> body) throws Exception {
        String protocol = WebUtil.getProtocol(request) + Strings.COLON + Strings.DOUBLE_SLASH;
        String targetUrl = getTargetUrl(protocol, request);
        jump(request, response, targetUrl, body);
    }

    protected String getTargetUrl(String protocol, HttpServletRequest request) {
        String url = WebUtil.getRelativeRequestUrl(request);
        int index = StringUtils.ordinalIndexOf(url, Strings.SLASH, 3);
        String path = url.substring(index + 1);
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            path += Strings.QUESTION + queryString;
        }
        return protocol + path;
    }

    protected abstract void jump(HttpServletRequest request, HttpServletResponse response, String targetUrl,
            Map<String, Object> body) throws Exception;

}
