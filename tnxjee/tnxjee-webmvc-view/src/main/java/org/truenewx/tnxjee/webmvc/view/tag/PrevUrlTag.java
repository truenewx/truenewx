package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.webmvc.view.util.WebViewUtil;

/**
 * 输出前一个请求URL的标签
 *
 * @author jianglei
 */
public class PrevUrlTag extends TagSupport {

    private static final long serialVersionUID = 8123676932054182255L;

    private String defaultHref = "javascript:history.back(-1)";
    private boolean withContext = true;

    public void setDefault(String defaultHref) {
        this.defaultHref = defaultHref;
    }

    public void setContext(boolean context) {
        this.withContext = context;
    }

    @Override
    public int doEndTag() throws JspException {
        // 使用pageContext中的request会得到jsp页面的访问路径，这可能导致错误，所以从Spring上下文中获取request
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            String prevUrl = WebViewUtil.getPreviousUrl(request);
            JspWriter out = this.pageContext.getOut();
            try {
                if (prevUrl != null) {
                    if (this.withContext) {
                        String contextPath = request.getContextPath();
                        if (!contextPath.equals(Strings.SLASH)) {
                            out.print(contextPath);
                        }
                    }
                    out.print(prevUrl);
                } else {
                    out.print(this.defaultHref);
                }
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
        return EVAL_PAGE;
    }
}
