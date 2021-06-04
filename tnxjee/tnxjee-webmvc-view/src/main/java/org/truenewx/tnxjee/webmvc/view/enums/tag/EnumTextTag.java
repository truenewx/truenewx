package org.truenewx.tnxjee.webmvc.view.enums.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.webmvc.view.tagext.UiTagSupport;

/**
 * 枚举文本标签
 *
 * @author jianglei
 */
public class EnumTextTag extends UiTagSupport {
    private String type;
    private String subtype;
    private String value;

    public void setType(String type) {
        this.type = type;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public void setValue(Object value) {
        this.value = value.toString();
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (this.value != null) {
            EnumDictResolver enumDictResolver = getBeanFromApplicationContext(EnumDictResolver.class);
            String caption = enumDictResolver.getText(this.type, this.subtype, this.value, getLocale());
            if (caption != null) {
                print(caption);
            }
        }
    }

}
