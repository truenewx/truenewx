package org.truenewx.tnxjee.webmvc.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * 抽象的控制器，为具体控制器类提供便利
 *
 * @author jianglei
 */
public abstract class AbstractController {
    /**
     * 获取当前HTTP请求
     *
     * @return 当前HTTP请求
     */
    protected HttpServletRequest getRequest() {
        return SpringWebContext.getRequest();
    }

    /**
     * 获取当前HTTP会话
     *
     * @return 当前HTTP会话
     */
    protected HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 设置请求属性，可用于向结果页面传递参数
     *
     * @param name  属性名
     * @param value 属性值
     */
    protected void setRequestAttribute(String name, Object value) {
        if (name != null && value != null) {
            getRequest().setAttribute(name, value);
        }
    }

    /**
     * 转换指定结果名为直接重定向的结果名
     *
     * @param result 结果名
     * @return 直接重定向的结果名
     */
    protected String getRedirectResult(String result) {
        return SpringWebMvcUtil.toRedirectResult(result);
    }
}
