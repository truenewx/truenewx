package org.truenewx.tnxjee.webmvc.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.HttpRequestMethod;
import org.truenewx.tnxjee.core.util.ExceptionUtil;
import org.truenewx.tnxjee.core.util.HttpClientUtil;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.jwt.JwtGenerator;

/**
 * RPC协助者
 *
 * @author jianglei
 */
@Component
public class RpcHelper {

    @Autowired
    private JwtGenerator generator;

    public Map<String, String> generateHeaders(String type, UserSpecificDetails<?> userDetails) {
        if (userDetails != null && this.generator.isAvailable()) {
            Map<String, String> headers = new HashMap<>();
            fillHeaders(headers, type, userDetails);
            return headers;
        }
        return null;
    }

    public void fillHeaders(Map<String, String> headers, String type, UserSpecificDetails<?> userDetails) {
        if (userDetails != null && this.generator.isAvailable()) {
            if (StringUtils.isNotBlank(type)) {
                headers.put(WebConstants.HEADER_RPC_TYPE, type);
            }
            String jwt = this.generator.generate(type, userDetails);
            headers.put(WebConstants.HEADER_RPC_JWT, jwt);
        }
    }

    public String call(HttpRequestMethod method, String url, Map<String, Object> params, String type,
            UserSpecificDetails<?> userSpecificDetails) {
        try {
            Map<String, String> headers = generateHeaders(type, userSpecificDetails);
            Binate<Integer, String> result = HttpClientUtil.request(method, url, params, headers,
                    Strings.ENCODING_UTF8);
            if (result != null && result.getLeft() == HttpStatus.SC_OK) {
                return result.getRight();
            }
        } catch (Exception e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
        return null;
    }

}
