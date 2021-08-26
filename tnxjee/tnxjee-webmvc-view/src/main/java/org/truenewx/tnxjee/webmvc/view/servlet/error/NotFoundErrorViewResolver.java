package org.truenewx.tnxjee.webmvc.view.servlet.error;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.webmvc.view.exception.resolver.ViewErrorPathProperties;

/**
 * 404错误视图解决器
 */
@Component
public class NotFoundErrorViewResolver implements ErrorViewResolver {

    private String viewName;

    @Autowired
    public NotFoundErrorViewResolver(WebMvcProperties mvcProperties, ViewErrorPathProperties errorPathProperties) {
        // 确保页面路径正确地用/分隔，且不出现连续的//
        String prefix = mvcProperties.getView().getPrefix();
        if (prefix == null) {
            prefix = Strings.EMPTY;
        }
        String path = errorPathProperties.getNotFound();
        if (prefix.endsWith(Strings.SLASH)) {
            if (path.startsWith(Strings.SLASH)) {
                this.viewName = path.substring(1);
            } else {
                this.viewName = path;
            }
        } else {
            if (path.startsWith(Strings.SLASH)) {
                this.viewName = path;
            } else {
                this.viewName = Strings.SLASH + path;
            }
        }
    }

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (status == HttpStatus.NOT_FOUND) {
            return new ModelAndView(this.viewName, model);
        }
        return null;
    }

}
