package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 字符串内容转换为HTML格式内容的标签
 *
 * @author jianglei
 */
public class ToHtmlTag extends SimpleTagSupport {

    private String value;
    private boolean notNull;

    public void setValue(String value) {
        this.value = value;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    @Override
    public void doTag() throws JspException, IOException {
        String value = this.value;
        if (StringUtils.isEmpty(value)) {
            value = this.notNull ? Strings.SPACE : Strings.EMPTY;
        }
        value = value.replaceAll(Strings.SPACE, "&nbsp;");
        value = value.replaceAll("\n", "<br/>");
        getJspContext().getOut().print(value);
    }

}
