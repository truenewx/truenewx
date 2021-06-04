package org.truenewx.tnxjee.webmvc.security.web.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;

/**
 * 已登录凭证控制器
 */
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private WebAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 判断当前是否已登录，匿名即可访问
     *
     * @return 是否已登录
     */
    @GetMapping("/authorized")
    @ConfigAnonymous
    public boolean isAuthorized() {
        return SecurityUtil.getAuthorizedUserIdentity() != null;
    }

    /**
     * 校验是否已登录，已登录访问不做任何处理，匿名访问则由框架抛出错误
     */
    @GetMapping("/validate")
    @ConfigAuthority
    public void validate() {
    }

    /**
     * 获取登录地址，已登录则返回null，未登录才返回登录地址
     *
     * @return 登录地址
     */
    @GetMapping("/login-url")
    @ConfigAnonymous
    public String getLoginUrl() {
        return isAuthorized() ? null : this.authenticationEntryPoint.getLoginFormUrl();
    }

}
