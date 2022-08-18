package org.truenewx.tnxjee.webmvc.view.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.truenewx.tnxjee.webmvc.controller.JumpControllerSupport;

/**
 * 直接重定向控制器支持
 */
@RequestMapping("/redirect")
public abstract class RedirectControllerSupport extends JumpControllerSupport {

    @Autowired
    private RedirectStrategy redirectStrategy;

    @Override
    protected void jump(HttpServletRequest request, HttpServletResponse response, String targetUrl,
            Map<String, Object> body) throws Exception {
        this.redirectStrategy.sendRedirect(request, response, targetUrl);
    }

}
