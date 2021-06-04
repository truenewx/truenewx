package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.view.tagext.SimpleDynamicAttributeTagSupport;

/**
 * 转调控件
 *
 * @author jianglei
 */
public class IncludeTag extends SimpleDynamicAttributeTagSupport {

    /**
     * 转调缓存
     */
    public static final String INCLUDE_CACHED = "_APPLICATION_INCLUDE_CACHED";
    /**
     * 是否缓存
     */
    private boolean cached;
    private String url;

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doTag() throws JspException, IOException {
        HttpServletRequest request = getRequest();
        ServletContext servletContext = request.getServletContext();
        if (this.url.startsWith(Strings.SLASH)) {
            String host = WebUtil.getHost(request, true);
            this.url = request.getScheme() + "://" + host + this.url;
        }
        try {
            String result;
            if (this.cached) { // 需数据缓存
                Object includeCached = servletContext.getAttribute(INCLUDE_CACHED);
                Map<String, String> cachedMap = null;
                if (includeCached != null) {
                    cachedMap = (Map<String, String>) includeCached;
                } else {
                    cachedMap = new HashMap<>();
                }
                result = cachedMap.get(this.url);
                if (StringUtils.isEmpty(result)) {
                    result = NetUtil.requestByGet(this.url, this.attributes, null);
                    cachedMap.put(this.url, result);
                }
                servletContext.setAttribute(INCLUDE_CACHED, cachedMap);
            } else {
                result = NetUtil.requestByGet(this.url, this.attributes, null);
            }
            print(result);
        } catch (Throwable e) {
            // 任何异常均只打印堆栈日志，以避免影响页面整体显示
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }
}
