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
        String url = getUrl(request, status);
        if (url != null) {
            // 后台请求自身的错误页面地址，以获得被装饰后的页面内容，解决错误页面无法被SiteMesh装饰的问题
            // 尤其是404错误，经研究确认无论如何设置，均无法被SiteMesh装饰
            String content = NetUtil.requestByGet(url, null, Strings.ENCODING_UTF8);
            if (StringUtils.isNotBlank(content)) {
                try {
                    response.getWriter().println(content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    // 获取后台请求地址，由于此场景为非常低频的访问，故不进行内存缓存，节省内存空间
    private String getUrl(HttpServletRequest request, HttpStatus status) {
        String path = null;
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            path = this.pathProperties.getInternal();
        } else if (status == HttpStatus.NOT_FOUND) {
            path = this.pathProperties.getNotFound();
        }
        if (exists(path)) {
            return "http://localhost:" + request.getServerPort() + path;
        }
        return null;
    }

    private boolean exists(String path) {
        if (path != null) {
            WebMvcProperties.View viewProperties = this.mvcProperties.getView();
            path = viewProperties.getPrefix() + path + viewProperties.getSuffix();
            path = path.replaceAll("//", Strings.SLASH);
            WebContextResource resource = new WebContextResource(path);
            return resource.exists();
        }
        return false;
    }

    @GetMapping("/*")
    public String get(HttpServletRequest request) {
        return WebUtil.getRelativeRequestAction(request);
    }

}
