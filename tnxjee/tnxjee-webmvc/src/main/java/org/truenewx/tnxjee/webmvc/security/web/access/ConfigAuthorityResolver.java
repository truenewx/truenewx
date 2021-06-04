package org.truenewx.tnxjee.webmvc.security.web.access;

import java.util.Collection;

import org.springframework.http.HttpMethod;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;

/**
 * 配置权限解决器
 */
public interface ConfigAuthorityResolver {

    /**
     * 解决指定请求访问所需的配置权限
     *
     * @param uri    请求路径
     * @param method 请求方法
     * @return 配置权限集
     */
    Collection<UserConfigAuthority> resolveConfigAuthorities(String uri, HttpMethod method);

}
