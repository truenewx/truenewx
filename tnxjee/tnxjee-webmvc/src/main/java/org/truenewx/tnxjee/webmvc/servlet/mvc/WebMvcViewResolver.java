package org.truenewx.tnxjee.webmvc.servlet.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * MVC视图解决器
 */
@Component
public class WebMvcViewResolver {

    @Autowired
    private WebMvcProperties webMvcProperties;

    public void resolveView(HttpServletRequest request, HttpServletResponse response, String viewName,
            @NotNull Map<String, Object> model) throws IOException, ServletException {
        boolean redirect = viewName.startsWith(SpringWebMvcUtil.REDIRECT_VIEW_NAME_PREFIX);
        String path = redirect ? viewName.substring(SpringWebMvcUtil.REDIRECT_VIEW_NAME_PREFIX.length()) : viewName;
        if (!path.startsWith(Strings.SLASH)) {
            path = Strings.SLASH + path;
        }
        if (redirect) {
            path = NetUtil.mergeParams(path, model, Strings.ENCODING_UTF8);
            response.sendRedirect(path);
        } else {
            WebMvcProperties.View view = this.webMvcProperties.getView();
            path = view.getPrefix() + path + view.getSuffix();
            path = path.replaceAll(Strings.DOUBLE_SLASH, Strings.SLASH); // 避免出现双斜杠//
            if (model != null) {
                model.forEach(request::setAttribute);
            }
            request.getRequestDispatcher(path).forward(request, response);
        }
    }

}
