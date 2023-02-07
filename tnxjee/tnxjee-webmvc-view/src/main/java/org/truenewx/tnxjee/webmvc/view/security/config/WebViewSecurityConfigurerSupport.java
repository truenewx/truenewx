package org.truenewx.tnxjee.webmvc.view.security.config;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.webmvc.security.config.annotation.web.configuration.WebMvcSecurityConfigurerSupport;
import org.truenewx.tnxjee.webmvc.view.controller.RedirectControllerSupport;
import org.truenewx.tnxjee.webmvc.view.exception.resolver.ViewResolvableExceptionResolver;

/**
 * WEB视图层安全配置支持
 */
public abstract class WebViewSecurityConfigurerSupport extends WebMvcSecurityConfigurerSupport {

    @Autowired
    private ViewResolvableExceptionResolver viewResolvableExceptionResolver;
    @Autowired
    private WebMvcProperties mvcProperties;

    @Bean
    @Override
    public AccessDeniedHandler accessDeniedHandler() {
        AccessDeniedHandlerImpl accessDeniedHandler = (AccessDeniedHandlerImpl) super.accessDeniedHandler();
        String prefix = this.mvcProperties.getView().getPrefix();
        if (StringUtils.isNotBlank(prefix)) {
            prefix = NetUtil.standardizeUri(prefix);
        } else {
            prefix = Strings.EMPTY;
        }
        String businessErrorPath = this.viewResolvableExceptionResolver.getBusinessErrorPath();
        if (StringUtils.isNotBlank(businessErrorPath)) {
            businessErrorPath = NetUtil.standardizeUri(businessErrorPath);
        } else {
            businessErrorPath = Strings.EMPTY;
        }
        String errorPage = prefix + businessErrorPath + this.mvcProperties.getView().getSuffix();
        if (StringUtils.isNotBlank(errorPage)) {
            accessDeniedHandler.setErrorPage(errorPage);
        }
        return accessDeniedHandler;
    }

    /**
     * 获取安全框架忽略的URL ANT样式集合
     *
     * @return 安全框架忽略的URL ANT样式集合
     */
    @Override
    protected Collection<String> getIgnoringAntPatterns() {
        Collection<String> patterns = super.getIgnoringAntPatterns();
        patterns.add(getIgnoringAntPatternFromController(RedirectControllerSupport.class));
        // 静态资源全部忽略
        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (StringUtils.isNotBlank(staticPathPattern)) {
            String[] staticPathPatterns = StringUtil.splitAndTrim(staticPathPattern, Strings.COMMA);
            Collections.addAll(patterns, staticPathPatterns);
        }
        return patterns;
    }

}
