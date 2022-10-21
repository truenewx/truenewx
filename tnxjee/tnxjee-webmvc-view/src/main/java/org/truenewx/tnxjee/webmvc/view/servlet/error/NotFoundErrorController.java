package org.truenewx.tnxjee.webmvc.view.servlet.error;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.function.WebContextPathPredicate;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;
import org.truenewx.tnxjee.webmvc.view.exception.resolver.ViewErrorPathProperties;
import org.truenewx.tnxjee.webmvc.view.util.WebViewUtil;

@Controller
public class NotFoundErrorController extends BasicErrorController {

    private static final String PARAMETER_PATH = "path";
    private static final String PARAMETER_PREV = "prev";

    @Autowired
    private WebMvcProperties mvcProperties;
    @Autowired
    private ViewErrorPathProperties pathProperties;
    @Autowired
    private WebContextPathPredicate webContextPathPredicate;

    @Autowired
    public NotFoundErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    @Override
    protected ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
            Map<String, Object> model) {
        if (!this.mvcProperties.isThrowExceptionIfNoHandlerFound() && status == HttpStatus.NOT_FOUND) {
            // 获取后台请求地址，由于此场景为非常低频的访问，故不进行内存缓存，节省内存空间
            String path = this.pathProperties.getNotFound();
            if (supports(path)) {
                if (!path.startsWith(SpringWebMvcUtil.REDIRECT_VIEW_NAME_PREFIX)) {
                    String prefix = this.mvcProperties.getView().getPrefix();
                    if (prefix.endsWith(Strings.SLASH) && path.startsWith(Strings.SLASH)) {
                        path = path.substring(1);
                    } else if (!prefix.endsWith(Strings.SLASH) && !path.startsWith(Strings.SLASH)) {
                        path = Strings.SLASH + path;
                    }
                }
                Map<String, Object> params = new HashMap<>();
                params.put(PARAMETER_PATH, model.get(PARAMETER_PATH));
                String prev = WebViewUtil.getPreviousUrl(request);
                if (prev != null) {
                    params.put(PARAMETER_PREV, prev);
                }
                return new ModelAndView(path, params);
            }
        }
        return null;
    }

    protected boolean supports(String path) {
        if (path.startsWith(SpringWebMvcUtil.REDIRECT_VIEW_NAME_PREFIX)) {
            path = path.substring(SpringWebMvcUtil.REDIRECT_VIEW_NAME_PREFIX.length());
        }
        return this.webContextPathPredicate.test(path);
    }

    @GetMapping("/{status}")
    @ConfigAnonymous
    public ModelAndView get(@PathVariable("status") int status, HttpServletRequest request) {
        String action = WebUtil.getRelativeRequestAction(request);
        ModelAndView mav = new ModelAndView(action);
        mav.addObject("status", status);
        return mav;
    }

}
