package org.truenewx.tnxjeex.cas.server.service;

/**
 * CAS服务器异常错误码集
 */
public class CasServerExceptionCodes {

    private CasServerExceptionCodes() {
    }

    /**
     * 无效的service参数
     */
    public static final String INVALID_SERVICE = "error.cas.server.invalid_service";

    /**
     * 无效的应用票据
     */
    public static final String INVALID_APP_TICKET = "error.cas.server.invalid_app_ticket";

    /**
     * 不支持的登录认证方式
     */
    public static final String UNSUPPORTED_AUTHENTICATE_MODE = "error.cas.server.unsupported_authenticate_mode";

}
