package org.truenewx.tnxjee.webmvc.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.HttpRequestMethod;
import org.truenewx.tnxjee.core.util.ExceptionUtil;
import org.truenewx.tnxjee.core.util.HttpClientUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.service.exception.ResolvableException;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.exception.parser.ResolvableExceptionParser;
import org.truenewx.tnxjee.webmvc.jwt.JwtGenerator;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;

/**
 * RPC支持
 *
 * @author jianglei
 */
public abstract class AbstractRpcInvoker implements WebRpcInvoker {

    @Autowired
    private JwtGenerator generator;
    @Autowired
    private ResolvableExceptionParser resolvableExceptionParser;

    protected UserSpecificDetails<?> getUserDetails(String type) {
        return SecurityUtil.getAuthorizedUserDetails();
    }

    @Override
    public Map<String, String> generateHeaders(String type) {
        Map<String, String> headers = new HashMap<>();
        UserSpecificDetails<?> userDetails = getUserDetails(type);
        if (userDetails != null && this.generator.isAvailable()) {
            if (StringUtils.isNotBlank(type)) {
                headers.put(WebConstants.HEADER_RPC_TYPE, type);
            }
            String jwt = this.generator.generate(type, userDetails);
            headers.put(WebConstants.HEADER_RPC_JWT, jwt);
        }
        return headers;
    }

    @Override
    public String invoke(HttpRequestMethod method, String url, Map<String, Object> params, String type) {
        try {
            Map<String, String> headers = generateHeaders(type);
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
            handleException(e);
        }
        return null;
    }

    protected void handleException(Exception e) {
        RuntimeException re = ExceptionUtil.toRuntimeException(e);
        LogUtil.error(getClass(), re);
        throw re;
    }

    @Override
    public void download(HttpServletResponse response, String url, Map<String, Object> params, String type) {
        Map<String, String> headers = generateHeaders(type);
        try {
            HttpClientUtil.download(url, params, headers, (responseEntity, responseHeaders) -> {
                try {
                    responseHeaders.forEach(response::setHeader);
                    response.setContentLengthLong(responseEntity.getContentLength());
                    responseEntity.writeTo(response.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
