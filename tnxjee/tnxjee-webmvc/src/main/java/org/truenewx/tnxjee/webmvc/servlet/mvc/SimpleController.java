package org.truenewx.tnxjee.webmvc.servlet.mvc;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.truenewx.tnxjee.core.Strings;

/**
 * 简单Controller
 *
 * @author jianglei
 */
public abstract class SimpleController extends AbstractController {

    /**
     * 获取默认的视图名
     *
     * @return 默认的视图名
     */
    protected final String getDefaultViewName() {
        final RequestMapping rm = this.getClass().getAnnotation(RequestMapping.class);
        if (rm != null) {
            final String[] values = rm.value();
            if (values.length > 0) {
                if (values[0].startsWith(Strings.SLASH)) {
                    return values[0].substring(1);
                }
                return values[0];
            }
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return getDefaultViewName();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post() {
        return get();
    }

}
