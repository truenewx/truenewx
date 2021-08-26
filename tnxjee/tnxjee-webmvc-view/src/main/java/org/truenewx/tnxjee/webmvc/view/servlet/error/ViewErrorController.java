package org.truenewx.tnxjee.webmvc.view.servlet.error;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.io.WebContextResource;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.view.exception.resolver.ViewErrorPathProperties;

@Controller
@RequestMapping("/error")
public class ViewErrorController extends BasicErrorController {

    @Autowired
    private WebMvcProperties mvcProperties;
    @Autowired
    private ViewErrorPathProperties pathProperties;

    @Autowired
    public ViewErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    @Override
    protected ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
            Map<String, Object> model) {
        if (status == HttpStatus.NOT_FOUND) {
            String path = this.pathProperties.getNotFound();
            if (supports(path)) {
                String url = "http://localhost:" + request.getServerPort() + path;
                String content = NetUtil.requestByGet(url, null, Strings.ENCODING_UTF8);
                if (StringUtils.isNotBlank(content)) {
                    try {
                        response.getWriter().println(content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }

    private boolean supports(String path) {
        WebMvcProperties.View viewProperties = this.mvcProperties.getView();
        path = viewProperties.getPrefix() + path + viewProperties.getSuffix();
        path = path.replaceAll("//", Strings.SLASH);
        WebContextResource resource = new WebContextResource(path);
        return resource.exists();
    }

    @GetMapping("/*")
    public String get(HttpServletRequest request) {
        return WebUtil.getRelativeRequestAction(request);
    }

}
