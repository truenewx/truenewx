package org.truenewx.tnxjee.webmvc.config;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.web.cors.CorsRegistryProperties;
import org.truenewx.tnxjee.web.util.SwaggerUtil;
import org.truenewx.tnxjee.web.util.WebConstants;
import org.truenewx.tnxjee.webmvc.cors.IgnoreNullConfigCorsProcessor;
import org.truenewx.tnxjee.webmvc.cors.SingleCorsConfigurationSource;
import org.truenewx.tnxjee.webmvc.jwt.JwtGenerator;
import org.truenewx.tnxjee.webmvc.jwt.JwtGeneratorImpl;
import org.truenewx.tnxjee.webmvc.jwt.JwtParser;
import org.truenewx.tnxjee.webmvc.jwt.JwtParserImpl;

/**
 * WEB MVC配置器支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
public abstract class WebMvcConfigurerSupport implements WebMvcConfigurer {
    @Autowired
    private CommonProperties commonProperties;
    @Autowired
    private CorsRegistryProperties corsRegistryProperties;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SingleCorsConfigurationSource corsConfigurationSource;
    @Autowired
    private IgnoreNullConfigCorsProcessor ignoreNullConfigCorsProcessor;

    protected final ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter -> {
            // 移除多余的MappingJackson2HttpMessageConverter，已被JacksonHttpMessageConverter取代
            return converter.getClass() == MappingJackson2HttpMessageConverter.class;
        });
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Map<String, HandlerInterceptor> interceptors = getApplicationContext().getBeansOfType(HandlerInterceptor.class);
        interceptors.forEach((name, interceptor) -> {
            registry.addInterceptor(interceptor);
        });
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (SwaggerUtil.isEnabled(getApplicationContext())) {
            registry.addResourceHandler("/swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置的应用URI均允许跨域访问
        Set<String> allowedOriginals = new HashSet<>(this.commonProperties.getAllAppUris());
        // 加入额外配置的跨域访问白名单
        CollectionUtil.addAll(allowedOriginals, this.corsRegistryProperties.getAllowedOrigins());
        boolean all = allowedOriginals.removeIf(Strings.ASTERISK::equals);

        CorsRegistration registration = registry
                .addMapping(this.corsRegistryProperties.getPathPattern())
                .allowedOrigins(allowedOriginals.toArray(new String[0]))
                .allowedMethods(this.corsRegistryProperties.getAllowedMethods())
                .allowedHeaders(this.corsRegistryProperties.getAllowedHeaders())
                .allowCredentials(true);
        String[] exposedHeaders = this.corsRegistryProperties.getExposedHeaders();
        Set<String> exposedHeaderSet = new HashSet<>();
        addExposedHeaders(exposedHeaderSet);
        exposedHeaders = ArrayUtils.addAll(exposedHeaders, exposedHeaderSet.toArray(new String[0]));
        registration.exposedHeaders(exposedHeaders);
        if (this.corsRegistryProperties.getMaxAge() != null) {
            registration.maxAge(this.corsRegistryProperties.getMaxAge());
        }
        CorsConfiguration corsConfiguration = BeanUtil.getFieldValue(registration, CorsConfiguration.class);
        if (all) {
            corsConfiguration.addAllowedOriginPattern(Strings.ASTERISK);
        }
        this.corsConfigurationSource.setCorsConfiguration(corsConfiguration);
    }

    protected void addExposedHeaders(Collection<String> exposedHeaders) {
        exposedHeaders.add(WebConstants.HEADER_REDIRECT_TO);
        exposedHeaders.add(WebConstants.HEADER_LOGIN_URL);
        exposedHeaders.add(WebConstants.HEADER_ORIGINAL_REQUEST);
    }

    @Bean("corsFilter")
    public CorsFilter corsFilter() {
        CorsFilter corsFilter = new CorsFilter(this.corsConfigurationSource);
        corsFilter.setCorsProcessor(this.ignoreNullConfigCorsProcessor);
        return corsFilter;
    }

    @Bean
    @ConditionalOnMissingBean(JwtGenerator.class)
    public JwtGenerator jwtGenerator() {
        return new JwtGeneratorImpl();
    }

    @Bean
    @ConditionalOnMissingBean(JwtParser.class)
    public JwtParser jwtParser() {
        return new JwtParserImpl();
    }

}
