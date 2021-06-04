package org.truenewx.tnxjee.webmvc.security.config.annotation.web.configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.cors.CorsRegistryProperties;
import org.truenewx.tnxjee.web.security.WebSecurityProperties;
import org.truenewx.tnxjee.web.util.SwaggerUtil;
import org.truenewx.tnxjee.webmvc.api.meta.ApiMetaController;
import org.truenewx.tnxjee.webmvc.security.access.UserAuthorityAccessDecisionManager;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjee.webmvc.security.web.access.AccessDeniedBusinessExceptionHandler;
import org.truenewx.tnxjee.webmvc.security.web.access.intercept.WebFilterInvocationSecurityMetadataSource;
import org.truenewx.tnxjee.webmvc.security.web.authentication.InternalJwtAuthenticationFilter;
import org.truenewx.tnxjee.webmvc.security.web.authentication.WebAuthenticationEntryPoint;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;

/**
 * WebMvc安全配置器支持
 */
// 安全配置器与MVC配置器如果合并在同一个类中，webmvc-view工程启动时无法即时注入配置属性实例，导致启动失败
@EnableWebSecurity
public abstract class WebMvcSecurityConfigurerSupport extends WebSecurityConfigurerAdapter {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private RedirectStrategy redirectStrategy;
    @Autowired
    private WebSecurityProperties securityProperties;
    @Autowired
    private CorsRegistryProperties corsRegistryProperties;

    protected SecurityUrlProvider urlProvider = new SecurityUrlProvider() {
        // 所有方法都有默认实现，默认实例无需提供
    };

    @Autowired(required = false)
    public void setUrlProvider(SecurityUrlProvider urlProvider) {
        this.urlProvider = urlProvider;
    }

    /**
     * 获取访问资源需要具备的权限
     */
    @Bean
    public WebFilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new WebFilterInvocationSecurityMetadataSource();
    }

    /**
     * 匿名用户试图访问登录用户才能访问的资源后的错误处理
     */
    @Bean
    public WebAuthenticationEntryPoint authenticationEntryPoint() {
        return new WebAuthenticationEntryPoint(this.urlProvider.getDefaultLoginFormUrl());
    }

    /**
     * 登录用户访问资源的权限判断
     */
    @Bean
    public UserAuthorityAccessDecisionManager accessDecisionManager() {
        return new UserAuthorityAccessDecisionManager();
    }

    /**
     * 登录用户越权访问资源后的错误处理
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedBusinessExceptionHandler();
    }

    /**
     * 登出成功后的处理
     */
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        SimpleUrlLogoutSuccessHandler successHandler = new SimpleUrlLogoutSuccessHandler();
        successHandler.setRedirectStrategy(this.redirectStrategy);
        String logoutSuccessUrl = this.urlProvider.getLogoutSuccessUrl();
        if (logoutSuccessUrl != null) {
            successHandler.setDefaultTargetUrl(logoutSuccessUrl);
        }
        return successHandler;
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        HttpSecurity http = getHttp();

        web.addSecurityFilterChainBuilder(http).postBuildAction(() -> {
            FilterSecurityInterceptor interceptor = http.getSharedObject(FilterSecurityInterceptor.class);
            WebFilterInvocationSecurityMetadataSource metadataSource = securityMetadataSource();
            FilterInvocationSecurityMetadataSource originalMetadataSource = interceptor.getSecurityMetadataSource();
            if (!(originalMetadataSource instanceof WebFilterInvocationSecurityMetadataSource)) {
                metadataSource.setOrigin(originalMetadataSource);
            }
            interceptor.setSecurityMetadataSource(metadataSource);
            interceptor.setAccessDecisionManager(accessDecisionManager());
            web.securityInterceptor(interceptor);
        });

        web.ignoring().antMatchers(getIgnoringAntPatterns().toArray(new String[0]));
    }

    /**
     * 获取安全框架忽略的URL ANT样式集合
     *
     * @return 安全框架忽略的URL ANT样式集合
     */
    protected Collection<String> getIgnoringAntPatterns() {
        Collection<String> patterns = new HashSet<>();
        patterns.add(getIgnoringAntPatternFromController(ApiMetaController.class));

        if (SwaggerUtil.isEnabled(getApplicationContext())) {
            patterns.add("/swagger-ui.html");
            patterns.add("/webjars/**");
            patterns.add("/v2/api-docs");
            patterns.add("/swagger-resources/**");
        }

        if (this.securityProperties != null) {
            List<String> ignoringPatterns = this.securityProperties.getIgnoringPatterns();
            if (ignoringPatterns != null) {
                patterns.addAll(ignoringPatterns);
            }
        }
        return patterns;
    }

    protected final String getIgnoringAntPatternFromController(Class<?> controllerClass) {
        RequestMapping mapping = controllerClass.getAnnotation(RequestMapping.class);
        return mapping.value()[0] + "/**";
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 覆盖父类的方法实现，且不调用父类方法实现，以标记AuthenticationManager由自定义创建，避免创建多个实例
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void configure(HttpSecurity http) throws Exception {
        // 应用登录配置器
        Collection<SecurityConfigurerAdapter> configurers = getSecurityConfigurerAdapters();
        for (SecurityConfigurerAdapter configurer : configurers) {
            http.apply(configurer);
        }
        http.addFilterAfter(new InternalJwtAuthenticationFilter(getApplicationContext()),
                UsernamePasswordAuthenticationFilter.class);

        RequestMatcher[] anonymousMatchers = getAnonymousRequestMatchers().toArray(new RequestMatcher[0]);
        // @formatter:off
        http.authorizeRequests().requestMatchers(anonymousMatchers).permitAll().anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .logout().logoutUrl(this.urlProvider.getLogoutProcessUrl()).logoutSuccessHandler(logoutSuccessHandler())
                .deleteCookies(getLogoutClearCookies()).permitAll();
        // @formatter:on

        // 附加配置
        Map<String, WebHttpSecurityConfigurer> additionalConfigurers = getApplicationContext()
                .getBeansOfType(WebHttpSecurityConfigurer.class);
        for (WebHttpSecurityConfigurer configurer : additionalConfigurers.values()) {
            configurer.configure(http);
        }

        if (this.corsRegistryProperties.isAllowCredentials()) {
            http.cors().and().csrf().disable(); // 开启cors则必须关闭csrf，以允许跨站点请求
        } else if (this.securityProperties.isCsrfDisabled()) {
            http.csrf().disable();
        }
    }

    @SuppressWarnings({ "rawtypes" })
    protected Collection<SecurityConfigurerAdapter> getSecurityConfigurerAdapters() {
        return getApplicationContext().getBeansOfType(SecurityConfigurerAdapter.class).values();
    }

    /**
     * 获取经过安全框架控制，允许匿名访问的请求匹配器集合
     *
     * @return 可匿名访问的请求匹配器集合
     */
    protected Collection<RequestMatcher> getAnonymousRequestMatchers() {
        List<RequestMatcher> matchers = new ArrayList<>();
        matchers.add(new AntPathRequestMatcher("/error/**"));
        // 打开登录表单页面和登出的请求始终可匿名访问
        // 注意：不能将请求URL加入忽略清单中，如果加入，则请求将无法经过安全框架过滤器处理
        String loginFormUrl = this.urlProvider.getDefaultLoginFormUrl();
        if (loginFormUrl.startsWith(Strings.SLASH)) { // 相对路径才需要添加到匿名清单中
            int index = loginFormUrl.indexOf(Strings.QUESTION);
            if (index > 0) { // 去掉登录表单地址中可能的参数
                loginFormUrl = loginFormUrl.substring(0, index);
            }
            matchers.add(new AntPathRequestMatcher(loginFormUrl, HttpMethod.GET.name()));
        }
        String logoutProcessUrl = this.urlProvider.getLogoutProcessUrl();
        if (logoutProcessUrl.startsWith(Strings.SLASH)) { // 相对路径才需要添加到匿名清单中
            matchers.add(new AntPathRequestMatcher(logoutProcessUrl));
        }

        this.handlerMethodMapping.getAllHandlerMethods().forEach((action, handlerMethod) -> {
            Method method = handlerMethod.getMethod();
            if (Modifier.isPublic(method.getModifiers())) {
                ConfigAnonymous configAnonymous = method.getAnnotation(ConfigAnonymous.class);
                if (configAnonymous != null) {
                    HttpMethod httpMethod = action.getMethod();
                    String methodValue = httpMethod == null ? null : httpMethod.name();
                    RequestMatcher matcher;
                    String regex = configAnonymous.regex();
                    if (StringUtils.isNotBlank(regex)) { // 指定了正则表达式，则采用正则匹配器
                        matcher = new RegexRequestMatcher(regex, methodValue, true);
                    } else {
                        String pattern = action.getUri().replaceAll("\\{\\S+\\}", Strings.ASTERISK);
                        matcher = new AntPathRequestMatcher(pattern, methodValue);
                    }
                    matchers.add(matcher);
                }
            }
        });

        return matchers;
    }

    protected String[] getLogoutClearCookies() {
        return new String[]{ "JSESSIONID", "SESSION" };
    }

}
