package org.truenewx.tnxjee.webmvc.view.servlet.error;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.core.Strings;

/**
 * 简单的错误视图解决器
 */
public class SimpleErrorViewResolver implements ErrorViewResolver {

    private static final HttpStatus[] STATUSES = { HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR };

    private String prefix;

    public SimpleErrorViewResolver(WebMvcProperties mvcProperties) {
        // 确保页面路径中不出现连续的//
        String prefix = mvcProperties.getView().getPrefix();
        if (prefix == null) {
            prefix = Strings.EMPTY;
        }
        if (prefix.endsWith(Strings.SLASH)) {
            prefix = Strings.EMPTY;
        } else {
            prefix = Strings.SLASH;
        }
        this.prefix = prefix + "error/";
    }

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (ArrayUtils.contains(STATUSES, status)) {
            return new ModelAndView(this.prefix + status.value());
        }
        return null;
    }

}
