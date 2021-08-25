package org.truenewx.tnxjee.webmvc.view.config;

import javax.servlet.DispatcherType;

import org.apache.commons.lang3.StringUtils;
import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.webmvc.config.WebMvcConfigurerSupport;
import org.truenewx.tnxjee.webmvc.view.servlet.error.SimpleErrorViewResolver;
import org.truenewx.tnxjee.webmvc.view.servlet.filter.ForbidAccessFilter;
import org.truenewx.tnxjee.webmvc.view.servlet.resource.AntPatternResourceResolver;
import org.truenewx.tnxjee.webmvc.view.sitemesh.config.BuildableSiteMeshFilter;

/**
 * WEB视图层MVC配置支持
 */
public abstract class WebViewMvcConfigurerSupport extends WebMvcConfigurerSupport {

    @Autowired
    private WebMvcProperties mvcProperties;
    @Autowired
    private ResourceProperties resourceProperties;

    @Bean
    public FilterRegistrationBean<ForbidAccessFilter> forbidAccessFilter() {
        FilterRegistrationBean<ForbidAccessFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new ForbidAccessFilter());
        frb.addUrlPatterns("*.jsp");
        frb.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return frb;
    }

    @Bean
    public FilterRegistrationBean<BuildableSiteMeshFilter> siteMeshFilter() {
        FilterRegistrationBean<BuildableSiteMeshFilter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new BuildableSiteMeshFilter(this::buildSiteMeshFilter));
        frb.addUrlPatterns("/*");
        frb.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.ERROR);
        return frb;
    }

    @Bean
    @ConditionalOnMissingBean(ErrorViewResolver.class)
    public ErrorViewResolver errorViewResolver() {
        return new SimpleErrorViewResolver(this.mvcProperties);
    }

    protected void buildSiteMeshFilter(SiteMeshFilterBuilder builder) {
        builder.addExcludedPath("/swagger-ui.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);

        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (StringUtils.isNotBlank(staticPathPattern)) {
            String[] staticPathPatterns = StringUtil.splitAndTrim(staticPathPattern, Strings.COMMA);
            String[] staticLocations = this.resourceProperties.getStaticLocations();
            for (String pattern : staticPathPatterns) {
                if (!pattern.startsWith(Strings.SLASH)) { // 确保样式以/开头，否则判断一定不匹配
                    pattern = Strings.SLASH + pattern;
                }
                ResourceHandlerRegistration registration = registry.addResourceHandler(pattern);
                String dir = getStaticResourceDir(pattern);
                for (String staticLocation : staticLocations) {
                    if (staticLocation.endsWith(Strings.SLASH)) { // 确保不以/结尾
                        staticLocation = staticLocation.substring(0, staticLocation.length() - 1);
                    }
                    registration.addResourceLocations(staticLocation + dir);
                }
                if (AntPatternResourceResolver.supports(pattern)) {
                    registration.resourceChain(true).addResolver(new AntPatternResourceResolver(pattern));
                }
            }
            registry.setOrder(Ordered.HIGHEST_PRECEDENCE + 2000);
        }
    }

    /**
     * 获取指定静态资源路径Ant样式对应的资源文件存放目录
     *
     * @param pattern 资源路径Ant样式
     * @return 资源文件存放目录
     */
    protected String getStaticResourceDir(String pattern) {
        // 去掉开头的/**
        if (pattern.startsWith("/**/")) {
            return Strings.SLASH;
        }
        // 去掉*之后的部分
        int index = pattern.indexOf(Strings.ASTERISK);
        if (index >= 0) {
            pattern = pattern.substring(0, index);
            // 此时再去掉最后一个/之后的部分
            index = pattern.lastIndexOf(Strings.SLASH);
            if (index >= 0) {
                pattern = pattern.substring(0, index + 1);
            }
        }
        return pattern;
    }

}
