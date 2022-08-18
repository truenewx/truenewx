package org.truenewx.tnxjee.webmvc.view.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.web.SecurityUrlProvider;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * Web视图层工具类
 *
 * @author jianglei
 */
public class WebViewUtil {

    private WebViewUtil() {
    }

    public static void forward(ServletRequest request, ServletResponse response, String url)
            throws ServletException, IOException {
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * 直接重定向至指定URL。请求将被重置，POST请求参数将丢失，浏览器地址栏显示的URL将更改为指定URL。 URL如果为绝对路径，则必须以http://或https://开头
     *
     * @param request  请求
     * @param response 响应
     * @param url      URL
     * @throws IOException 如果重定向时出现IO错误
     */
    public static void redirect(HttpServletRequest request, HttpServletResponse response, String url)
            throws IOException {
        String location = url;
        if (!NetUtil.isHttpUrl(location, true)) {
            if (!location.startsWith(Strings.SLASH)) {
                location = Strings.SLASH + location;
            }
            String webRoot = request.getContextPath();
            if (!location.startsWith(webRoot)) {
                location = webRoot + location;
            }
        }
        response.sendRedirect(location);
    }

    public static String getPreviousUrl(HttpServletRequest request) {
        String prevUrl = getRelativePreviousUrl(request, true);
        if (prevUrl != null) {
            if (isLoginFormUrl(request, prevUrl)) { // 如果前一页为登录表单页，则执行默认的前一页规则，以避免跳转相同页
                prevUrl = null;
            } else {
                String action = WebUtil.getRelativeRequestAction(request);
                if (prevUrl.startsWith(action)) { // 如果前一页url以当前action开头，则执行默认的前一页规则，以避免跳转相同页
                    prevUrl = null;
                }
            }
        }
        return prevUrl;
    }

    public static boolean isLoginFormUrl(HttpServletRequest request, String url) {
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(request);
        SecurityUrlProvider securityUrlProvider = SpringUtil.getFirstBeanByClass(context, SecurityUrlProvider.class);
        if (securityUrlProvider != null) {
            String loginFormUrl = securityUrlProvider.getLoginFormUrl(request);
            return url.equals(loginFormUrl) || url.startsWith(loginFormUrl + Strings.QUESTION);
        }
        return false;
    }

    /**
     * 获取相对于web项目的前一个请求的URL
     *
     * @param request             请求
     * @param containsQueryString 是否需要包含请求参数
     * @return 前一个请求的URL
     */
    public static String getRelativePreviousUrl(HttpServletRequest request, boolean containsQueryString) {
        String referrer = request.getHeader("Referer");
        if (StringUtils.isNotBlank(referrer)) {
            String root = WebUtil.getProtocolAndHost(request);
            String contextPath = request.getContextPath();
            if (!contextPath.equals(Strings.SLASH)) {
                root += contextPath;
            }
            if (referrer.startsWith(root)) {
                String url = referrer.substring(root.length());
                if (!containsQueryString) {
                    int index = url.indexOf("?");
                    if (index > 0) {
                        url = url.substring(0, index);
                    }
                }
                return url;
            }
        }
        return null;
    }

}
