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
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.exception.parser.ResolvableExceptionParser;
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
    @Autowired
    private ResolvableExceptionParser resolvableExceptionParser;

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
            // RPC请求一律为AJAX请求
            headers.put(WebConstants.HEADER_AJAX_REQUEST, WebConstants.AJAX_REQUEST_VALUE);
            Binate<Integer, String> result = HttpClientUtil.request(method, url, params, headers,
                    Strings.ENCODING_UTF8);
            if (result != null) {
                int status = result.getLeft();
                String body = result.getRight();
                switch (status) {
                    case HttpStatus.SC_OK:
                        return body;
                    case HttpStatus.SC_FORBIDDEN:
                    case HttpStatus.SC_BAD_REQUEST:
                        ResolvableException e = this.resolvableExceptionParser.parse(body);
                        if (e != null) {
                            throw e;
                        }
                    default:
                        throw new RuntimeException(status + Strings.COLON + body);
                }
            }
        } catch (Exception e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
        return null;
    }

}
