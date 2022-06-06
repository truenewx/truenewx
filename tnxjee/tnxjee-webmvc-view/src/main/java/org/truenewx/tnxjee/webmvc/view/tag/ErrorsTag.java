package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.exception.model.ExceptionError;
import org.truenewx.tnxjee.webmvc.view.tagext.ErrorTagSupport;

/**
 * 输出错误消息的标签
 *
 * @author jianglei
 */
public class ErrorsTag extends ErrorTagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    private String delimiter = "<br>";

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public int doEndTag() throws JspException {
        StringBuffer message = new StringBuffer();
        List<ExceptionError> errors = getErrors();
        if (errors != null) {
            errors.forEach(error -> {
                if (Strings.ASTERISK.equals(this.field) || this.field.equals(error.getField())) {
                    message.append(this.delimiter).append(error.getMessage());
                }
            });
            if (message.length() > 0) {
                message.delete(0, this.delimiter.length());
            }
        }
        if (message.length() > 0) {
            JspWriter out = this.pageContext.getOut();
            try {
                out.print(message);
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
        return Tag.EVAL_PAGE;
    }

}
