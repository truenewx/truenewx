package org.truenewx.tnxjee.webmvc.context.request;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

/**
 * 动态OpenEntityManagerInView拦截器
 *
 * @author jianglei
 */
public class DynamicOpenEntityManagerInViewInterceptor extends OpenEntityManagerInViewInterceptor {

    private boolean supports(WebRequest request) {
        return true;
    }

    @Override
    public void preHandle(WebRequest request) throws DataAccessException {
        if (supports(request)) {
            super.preHandle(request);
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) {
        if (supports(request)) {
            super.postHandle(request, model);
        }
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws DataAccessException {
        if (supports(request)) {
            super.afterCompletion(request, ex);
        }
    }

    @Override
    public void afterConcurrentHandlingStarted(WebRequest request) {
        if (supports(request)) {
            super.afterConcurrentHandlingStarted(request);
        }
    }

}
