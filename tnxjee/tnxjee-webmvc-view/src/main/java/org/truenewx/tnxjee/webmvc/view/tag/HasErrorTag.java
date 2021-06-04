package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;

import org.truenewx.tnxjee.webmvc.view.tagext.ErrorTagSupport;

/**
 * 是否有错误消息的输出标签
 *
 * @author jianglei
 */
public class HasErrorTag extends ErrorTagSupport {

    private static final long serialVersionUID = -5895134371553366753L;

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        try {
            out.print(matches(false));
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }

}
