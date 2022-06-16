package org.truenewx.tnxjeex.cas.core.authentication.logout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;

/**
 * CAS登出成功处理器
 */
public abstract class CasLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    public CasLogoutSuccessHandler(SecurityUrlProvider urlProvider) {
        String logoutSuccessUrl = urlProvider.getLogoutSuccessUrl();
        if (logoutSuccessUrl != null) {
            setDefaultTargetUrl(logoutSuccessUrl);
        }
    }

    @Override
    @Autowired // 覆写以自动注入
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        super.setRedirectStrategy(redirectStrategy);
    }

    @Autowired
    public void setApiMetaProperties(ApiMetaProperties apiMetaProperties) {
        setTargetUrlParameter(apiMetaProperties.getRedirectTargetUrlParameter());
    }

}
