package org.truenewx.tnxjee.webmvc.view.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * 直接重定向控制器支持
 */
@RequestMapping("/redirect")
public abstract class RedirectControllerSupport {

    @Autowired
    private RedirectStrategy redirectStrategy;

    private void to(String protocol, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = WebUtil.getRelativeRequestAction(request);
        int index = StringUtils.ordinalIndexOf(action, Strings.SLASH, 3);
        String path = action.substring(index + 1);
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            path += Strings.QUESTION + queryString;
        }
        String url = protocol + path;
        this.redirectStrategy.sendRedirect(request, response, url);
    }

    @GetMapping("/to/**")
    public void to(HttpServletRequest request, HttpServletResponse response) throws IOException {
        to("//", request, response);
    }

    @GetMapping("/http/**")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        to("http://", request, response);
    }

    @GetMapping("/https/**")
    public void https(HttpServletRequest request, HttpServletResponse response) throws IOException {
        to("https://", request, response);
    }

}
