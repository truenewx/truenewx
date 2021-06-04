package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.webmvc.view.tagext.SimpleDynamicAttributeTagSupport;

/**
 * 截取字符串标签
 *
 * @author jianglei
 */
public class OmitTag extends SimpleDynamicAttributeTagSupport {

    private final static String REPLACE_OPERATOR = "...";

    private String value;
    private int size;

    public void setValue(String value) {
        this.value = value;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (StringUtils.isNotEmpty(this.value)) {
            if (0 < this.size && this.size < this.value.length()) {
                this.value = this.value.substring(0, this.size - 1) + REPLACE_OPERATOR;
            }
            print(this.value);
        }
    }
}
