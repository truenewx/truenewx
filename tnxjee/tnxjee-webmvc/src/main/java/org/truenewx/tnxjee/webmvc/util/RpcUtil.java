package org.truenewx.tnxjee.webmvc.util;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.truenewx.tnxjee.core.util.JacksonUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.util.WebConstants;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * RPC工具类
 */
public class RpcUtil {

    private static final String JWT_PREFIX = "jwt:";

    private RpcUtil() {
    }

    public static boolean isInternalRpc(HttpServletRequest request) {
        String internalJwt = request.getHeader(WebConstants.HEADER_INTERNAL_JWT);
        if (internalJwt != null) {
            return true;
        }
        String userAgent = request.getHeader(WebConstants.HEADER_USER_AGENT);
        return userAgent == null || userAgent.toLowerCase().startsWith("java");
    }

    public static String generateInternalJwt(UserSpecificDetails<?> userDetails, String secretKey,
            long expiredIntervalSeconds) {
        if (userDetails != null) {
            long expiredTimeMillis = System.currentTimeMillis() + expiredIntervalSeconds * 1000;
            try {
                String audienceJson = JacksonUtil.CLASSED_MAPPER.writeValueAsString(userDetails);
                String token = JWT.create()
                        .withExpiresAt(new Date(expiredTimeMillis))
                        .withAudience(audienceJson)
                        .sign(Algorithm.HMAC256(secretKey));
                return JWT_PREFIX + token;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static String getInternalJwt(HttpServletRequest request) {
        String jwt = request.getHeader(WebConstants.HEADER_INTERNAL_JWT);
        if (jwt != null && jwt.startsWith(JWT_PREFIX)) {
            return jwt.substring(JWT_PREFIX.length());
        }
        return null;
    }

}
