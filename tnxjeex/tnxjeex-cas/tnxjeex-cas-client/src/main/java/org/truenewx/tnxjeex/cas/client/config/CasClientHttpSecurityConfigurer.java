package org.truenewx.tnxjeex.cas.client.config;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.truenewx.tnxjee.webmvc.security.config.annotation.web.configuration.WebHttpSecurityConfigurer;

/**
 * Cas客户端HTTP安全配置器
 */
@Configuration
public class CasClientHttpSecurityConfigurer implements WebHttpSecurityConfigurer {

    @Autowired
    private CasClientProperties casClientProperties;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 在默认的登出过滤器之前插入单点登出过滤器，确保后者执行
        SingleSignOutFilter logoutFilter = new SingleSignOutFilter();
        logoutFilter.setCasServerUrlPrefix(this.casClientProperties.getServerContextUri(false));
        http.addFilterBefore(logoutFilter, LogoutFilter.class);
    }

    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> lrb = new ServletListenerRegistrationBean<>();
        lrb.setListener(new SingleSignOutHttpSessionListener());
        return lrb;
    }

}
