package org.truenewx.tnxjeex.openapi.client.web.security.authentication;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.web.authentication.AbstractAuthenticationTokenResolver;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatUser;
import org.truenewx.tnxjeex.openapi.client.service.wechat.WechatAppAccessor;

/**
 * 微信登录认证令牌解决器
 *
 * @author jianglei
 */
public abstract class WechatAuthenticationTokenResolver
        extends AbstractAuthenticationTokenResolver<WechatAuthenticationToken> {

    private static final String BODY_CACHE_KEY = WechatAuthenticationTokenResolver.class.getName() + ".body";
    private static final String PARAMETER_STATE = "state";
    private static final String UNDEFINED_STATE = "undefined";
    private static final String PARAMETER_CODE = "code";

    public WechatAuthenticationTokenResolver(String loginMode) {
        super(loginMode);
    }

    @Override
    public WechatAuthenticationToken resolveAuthenticationToken(HttpServletRequest request) {
        // 从state参数中解析参数放入请求属性中，以便于后续处理使用
        resolveState(request).forEach(request::setAttribute);
        WechatUser user = resolveUser(request);
        return new WechatAuthenticationToken(user);
    }

    public Map<String, Object> resolveState(HttpServletRequest request) {
        String state = getParam(request, PARAMETER_STATE);
        if (StringUtils.isNotBlank(state) && !UNDEFINED_STATE.equals(state)) {
            state = EncryptUtil.decryptByBase64(state);
            return JsonUtil.json2Map(state);
        }
        return Collections.emptyMap();
    }

    protected final String getParam(HttpServletRequest request, String paramName) {
        String paramValue = request.getParameter(paramName);
        if (StringUtils.isBlank(paramValue)) { // 从请求参数里取不到就到body里取
            Map<String, String> body = getRequestBody(request);
            paramValue = body.get(paramName);
        }
        return paramValue;
    }

    @SuppressWarnings("unchecked")
    protected final Map<String, String> getRequestBody(HttpServletRequest request) {
        Map<String, String> map = (Map<String, String>) request.getAttribute(BODY_CACHE_KEY);
        if (map == null) {
            map = WebUtil.getRequestBodyMap(request);
            request.setAttribute(BODY_CACHE_KEY, map);
        }
        return map;
    }

    public WechatUser resolveUser(HttpServletRequest request) {
        String loginCode = getParam(request, PARAMETER_CODE);
        return getAccessor().loadUser(loginCode);
    }

    protected abstract WechatAppAccessor getAccessor();

}
