package org.truenewx.tnxjee.webmvc.security.web.authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * 支持AJAX请求的授权成功处理器
 *
 * @author jianglei
 */
public abstract class AjaxAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public final void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        if (WebUtil.isAjaxRequest(request)) {
            Object result = getAjaxLoginResult(request, authentication);
            if (result != null) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().print(JsonUtil.toJson(result));
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    protected abstract Object getAjaxLoginResult(HttpServletRequest request, Authentication authentication);

}
