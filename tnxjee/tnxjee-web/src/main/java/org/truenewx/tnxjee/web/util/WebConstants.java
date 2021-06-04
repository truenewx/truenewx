package org.truenewx.tnxjee.web.util;

/**
 * Web常量类
 */
public class WebConstants {

    private WebConstants() {
    }

    /**
     * 头信息名：User-Agent
     */
    public static final String HEADER_USER_AGENT = "User-Agent";

    /**
     * 头信息名：AJAX请求
     */
    public static final String HEADER_AJAX_REQUEST = "X-Requested-With";

    /**
     * 头信息名：请求来源
     */
    public static final String HEADER_REFERER = "referer";

    /**
     * 头信息名：重定向目标地址
     */
    public static final String HEADER_REDIRECT_TO = "Redirect-To";

    /**
     * 头信息：登录地址
     */
    public static final String HEADER_LOGIN_URL = "Login-Url";
    /**
     * 头信息：原始请求
     */
    public static final String HEADER_ORIGINAL_REQUEST = "Original-Request";

    /**
     * 头信息名：内部JWT
     */
    public static final String HEADER_INTERNAL_JWT = "Internal-Jwt";

    /**
     * 默认的登录成功跳转目标参数
     */
    public static final String DEFAULT_LOGIN_SUCCESS_REDIRECT_PARAMETER = "_next";

}
