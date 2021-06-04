package org.truenewx.tnxjeex.cas.server.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.security.web.authentication.LoginViewResultResolver;
import org.truenewx.tnxjee.webmvc.security.web.authentication.ResolvableExceptionAuthenticationFailureHandler;
import org.truenewx.tnxjeex.cas.core.validation.constant.CasParameterNames;
import org.truenewx.tnxjeex.cas.server.service.CasServiceManager;
import org.truenewx.tnxjeex.cas.server.ticket.CasTicketManager;

/**
 * Cas服务端登录控制器
 */
@RequestMapping("/login")
public abstract class CasServerLoginControllerSupport {

    @Autowired
    private ResolvableExceptionAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private CasServiceManager serviceManager;
    @Autowired
    private CasTicketManager ticketManager;
    @Autowired
    private RedirectStrategy redirectStrategy;
    @Autowired
    private ApiMetaProperties apiMetaProperties;

    @GetMapping
    public ModelAndView get(@RequestParam(value = "service", required = false) String service,
            @RequestParam(value = "scope", required = false) String scope, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(service)) {
            service = getDefaultService();
            if (StringUtils.isBlank(service)) {
                return toBadServiceView(response);
            }
            // 写入请求属性中，便于后续获取
            request.setAttribute(CasParameterNames.SERVICE, service);
        }
        String redirectParameter = this.apiMetaProperties.getLoginSuccessRedirectParameter();
        if (WebUtil.isAjaxRequest(request)) {
            String originalRequest = request.getHeader(WebConstants.HEADER_ORIGINAL_REQUEST);
            if (originalRequest != null) {
                response.setHeader(WebConstants.HEADER_ORIGINAL_REQUEST, originalRequest);
            }
            if (this.ticketManager.checkTicketGrantingTicket(request)) {
                String targetUrl = this.serviceManager.getLoginProcessUrl(request, service, scope);
                if (originalRequest != null) {
                    String originalUrl = originalRequest.substring(originalRequest.indexOf(Strings.SPACE) + 1);
                    targetUrl = NetUtil.mergeParam(targetUrl, redirectParameter, originalUrl);
                }
                this.redirectStrategy.sendRedirect(request, response, targetUrl);
            } else { // AJAX登录只能进行自动登录，否则报401
                StringBuffer url = request.getRequestURL();
                StringUtil.ifNotBlank(request.getQueryString(), queryString -> {
                    url.append(Strings.QUESTION).append(queryString);
                });
                response.setHeader(WebConstants.HEADER_LOGIN_URL, url.toString()); // 将当前ajax请求URL作为登录URL返回
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return null;
        } else {
            if (this.ticketManager.checkTicketGrantingTicket(request)) {
                String targetUrl = this.serviceManager.getLoginProcessUrl(request, service, scope);
                String redirectUrl = request.getParameter(redirectParameter);
                if (StringUtils.isNotBlank(redirectUrl)) {
                    targetUrl = NetUtil.mergeParam(targetUrl, redirectParameter, redirectUrl);
                }
                this.redirectStrategy.sendRedirect(request, response, targetUrl);
                return null;
            }
            LoginViewResultResolver resultResolver = this.authenticationFailureHandler.getLoginViewResultResolver();
            String result = resultResolver == null ? null : resultResolver.resolveLoginViewResult(request);
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }
            return new ModelAndView(result);
        }
    }

    protected ModelAndView toBadServiceView(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Required String parameter 'service' is not present.");
        return null;
    }

    protected String getDefaultService() {
        String appName = getDefaultAppName();
        if (StringUtils.isNotBlank(appName)) {
            return this.serviceManager.getService(appName);
        }
        return null;
    }

    protected String getDefaultAppName() {
        return null;
    }

}
