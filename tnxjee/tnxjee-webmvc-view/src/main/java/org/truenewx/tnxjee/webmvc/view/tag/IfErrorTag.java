package org.truenewx.tnxjee.webmvc.view.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.truenewx.tnxjee.webmvc.view.tagext.ErrorTagSupport;

/**
 * 如果存在错误消息则执行标签体的标签
 *
 * @author jianglei
 */
public class IfErrorTag extends ErrorTagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    private boolean inverse;

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public int doStartTag() throws JspException {
        return matches(this.inverse) ? Tag.EVAL_BODY_INCLUDE : Tag.SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }
}
