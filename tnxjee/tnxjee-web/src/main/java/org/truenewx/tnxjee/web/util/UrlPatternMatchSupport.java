package org.truenewx.tnxjee.web.util;

import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * URL模版匹配支持
 *
 * @author jianglei
 */
public class UrlPatternMatchSupport {

    /**
     * 不可匿名访问的URL模板集
     */
    private String[] includeUrlPatterns;
    /**
     * 可匿名访问的URL模板集
     */
    private String[] excludeUrlPatterns;

    /**
     * @param includeUrlPatterns 不可匿名访问的URL模板集
     */
    public void setIncludeUrlPatterns(String[] includeUrlPatterns) {
        this.includeUrlPatterns = includeUrlPatterns;
    }

    /**
     * @param excludeUrlPatterns 可匿名访问的URL模板集
     */
    public void setExcludeUrlPatterns(String[] excludeUrlPatterns) {
        this.excludeUrlPatterns = excludeUrlPatterns;
    }

    protected boolean matches(String url) {
        if (this.includeUrlPatterns == null && this.excludeUrlPatterns == null) { // 所有url都作验证
            return true;
        } else if (this.includeUrlPatterns == null && this.excludeUrlPatterns != null) { // 只验证除exclude以外的url
            if (!StringUtil.wildcardMatchOneOf(url, this.excludeUrlPatterns)) {
                return true;
            }
        } else if (this.includeUrlPatterns != null && this.excludeUrlPatterns == null) { // 只验证include内的url
            if (StringUtil.wildcardMatchOneOf(url, this.includeUrlPatterns)) {
                return true;
            }
        } else if (this.includeUrlPatterns != null && this.excludeUrlPatterns != null) { // 验证include内及exclude以外的url
            if (StringUtil.wildcardMatchOneOf(url, this.includeUrlPatterns)
                    && !StringUtil.wildcardMatchOneOf(url, this.excludeUrlPatterns)) {
                return true;
            }
        }

        return false;
    }

}
