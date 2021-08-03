package org.truenewx.tnxjeex.cas.server.service;

import javax.servlet.http.HttpServletRequest;

/**
 * CAS服务管理器
 *
 * @author jianglei
 */
public interface CasServiceManager {

    String getAppName(String service);

    String getService(String appName);

    String getUri(HttpServletRequest request, String service);

    String getLoginProcessUrl(HttpServletRequest request, String service, String scope);

    String getLogoutProcessUrl(String service);

}
