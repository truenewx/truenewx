package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;

import org.truenewx.tnxjee.webmvc.view.tagext.ErrorTagSupport;

/**
 * 是否没有错误消息的输出标签
 *
 * @author jianglei
 */
public class NoErrorTag extends ErrorTagSupport {

    private static final long serialVersionUID = 4561458789317014954L;

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        try {
            out.print(matches(true));
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }

}
