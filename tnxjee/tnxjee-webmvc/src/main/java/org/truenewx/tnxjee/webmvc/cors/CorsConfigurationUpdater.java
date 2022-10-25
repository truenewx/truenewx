package org.truenewx.tnxjee.webmvc.cors;

/**
 * CORS配置修改器
 *
 * @author jianglei
 */
public interface CorsConfigurationUpdater {

    void addAllowedOrigin(String origin);

}
