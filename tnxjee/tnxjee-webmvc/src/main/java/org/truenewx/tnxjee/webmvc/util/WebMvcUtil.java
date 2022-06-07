package org.truenewx.tnxjee.webmvc.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.truenewx.tnxjee.web.util.WebConstants;

/**
 * Web MVC工具类
 */
public class WebMvcUtil {

    private WebMvcUtil() {
    }

    public static boolean isInternalRpc(HttpServletRequest request) {
        String internalJwt = request.getHeader(WebConstants.HEADER_RPC_JWT);
        if (internalJwt != null) {
            return true;
        }
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        return userAgent == null || userAgent.toLowerCase().startsWith("java");
    }

}
