package org.truenewx.tnxjee.webmvc.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.webmvc.bind.annotation.ResponseStream;

/**
 * Spring Web Mvc工具类
 *
 * @author jianglei
 */
public class SpringWebMvcUtil {

    /**
     * 直接重定向的视图名称前缀
     */
    public static final String REDIRECT_VIEW_NAME_PREFIX = "redirect:";

    private SpringWebMvcUtil() {
    }

    /**
     * 获取web项目应用范围内的ApplicationContext实例
     *
     * @param request HTTP请求
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext(HttpServletRequest request) {
        try {
            return RequestContextUtils.findWebApplicationContext(request);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * 获取web项目应用范围内的ApplicationContext实例
     *
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext() {
        return getApplicationContext(SpringWebContext.getRequest());
    }

    /**
     * 先尝试从Spring的LocaleResolver中获取区域，以便以自定义的方式获取区域
     *
     * @param request 请求
     * @return 区域
     */
    public static Locale getLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = SpringUtil
                .getFirstBeanByClass(getApplicationContext(request), LocaleResolver.class);
        if (localeResolver != null) {
            return localeResolver.resolveLocale(request);
        } else {
            return request.getLocale();
        }
    }

    public static boolean isResponseBody(HandlerMethod handlerMethod) {
        if (handlerMethod == null) {
            return false;
        }
        return handlerMethod.getReturnType().getParameterType() != ModelAndView.class
                && (handlerMethod.getMethodAnnotation(ResponseBody.class) != null
                || handlerMethod.getBeanType().getAnnotation(RestController.class) != null
                || handlerMethod.getMethodAnnotation(ResponseStream.class) != null);
    }

    public static String getRequestMappingUrl(HandlerMethod handlerMethod) {
        String url = getPath(handlerMethod.getBeanType().getAnnotation(RequestMapping.class));
        if (StringUtils.isBlank(url)) {
            url = Strings.EMPTY;
        }
        String path = getPath(handlerMethod.getMethodAnnotation(RequestMapping.class));
        if (StringUtils.isNotBlank(path)) {
            url += path;
        }
        return NetUtil.standardizeUrl(url);
    }

    private static String getPath(RequestMapping requestMapping) {
        return requestMapping == null ? null : ArrayUtil.get(requestMapping.value(), 0);
    }

    /**
     * 转换指定结果名为直接重定向的结果名
     *
     * @param result 结果名
     * @return 直接重定向的结果名
     */
    public static String toRedirectResult(String result) {
        return StringUtils.join(REDIRECT_VIEW_NAME_PREFIX, result);
    }
}
