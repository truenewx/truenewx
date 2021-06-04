package org.truenewx.tnxjee.webmvc.view.pager;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.tnxjee.webmvc.servlet.mvc.SimpleController;

/**
 *
 *
 * @author jianglei
 */
@Controller
public class PagerController extends SimpleController {

    @RequestMapping(value = "/pager")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> params = new HashMap<>();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                params.put(name, request.getParameter(name));
            }
            PagerUtil.output(request, response.getWriter(), params);
            return null;
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return null;
    }
}
