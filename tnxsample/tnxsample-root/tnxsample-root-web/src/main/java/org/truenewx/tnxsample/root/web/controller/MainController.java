package org.truenewx.tnxsample.root.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;

/**
 * 首页
 *
 * @author jianglei
 */
@Controller
public class MainController {

    private CsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();

    @RequestMapping("/")
    @ConfigAuthority
    public String root() {
        return index();
    }

    @RequestMapping("/index")
    @ConfigAuthority
    public String index() {
        return "/index";
    }

    @GetMapping("/swagger")
    @ConfigAnonymous(intranet = true)
    public String swagger() {
        return "redirect:/swagger-ui.html";
    }

    /**
     * 解决swagger-ui的/csrf的404问题
     */
    @GetMapping("/csrf")
    @ResponseBody
    @ConfigAnonymous
    public CsrfToken csrf(HttpServletRequest request) {
        return this.csrfTokenRepository.generateToken(request);
    }

}
