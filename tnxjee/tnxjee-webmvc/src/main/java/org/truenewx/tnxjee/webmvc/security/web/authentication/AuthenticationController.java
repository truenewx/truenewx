package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.service.security.access.GrantedAuthorityDecider;
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
    @Autowired
    private GrantedAuthorityDecider grantedAuthorityDecider;
    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String appName;

    @GetMapping("/authorized")
    @ConfigAnonymous
    public boolean isAuthorized() {
        return SecurityUtil.isAuthorized();
    }

    /**
     * 判断当前是否具有指定授权，匿名即可访问
     *
     * @return 是否具有指定授权
     */
    @GetMapping("/granted")
    @ConfigAnonymous
    public boolean isGranted(@RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "rank", required = false) String rank,
            @RequestParam(value = "permission", required = false) String permission) {
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityUtil.getGrantedAuthorities();
        if (CollectionUtils.isEmpty(grantedAuthorities)) {
            return false;
        }
        return this.grantedAuthorityDecider.isGranted(grantedAuthorities, type, rank, this.appName, permission);
    }

    /**
     * 校验登录用户访问权限，匿名用户由框架抛出异常
     */
    @GetMapping("/validate")
    @ConfigAuthority
    public void validate(@RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "rank", required = false) String rank,
            @RequestParam(value = "permission", required = false) String permission, HttpServletResponse response) {
        if (!isGranted(type, rank, permission)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * 获取登录地址，已登录则返回null，未登录才返回登录地址
     *
     * @return 登录地址
     */
    @GetMapping("/login-url")
    @ConfigAnonymous
    public String getLoginUrl(@RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "rank", required = false) String rank,
            @RequestParam(value = "permission", required = false) String permission) {
        return isGranted(type, rank, permission) ? null : this.authenticationEntryPoint.getLoginFormUrl();
    }

}
