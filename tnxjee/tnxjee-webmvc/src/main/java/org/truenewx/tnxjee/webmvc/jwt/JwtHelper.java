package org.truenewx.tnxjee.webmvc.jwt;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.util.WebConstants;

/**
 * JWT协助者
 *
 * @author jianglei
 */
@Component
public class JwtHelper {

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

}
