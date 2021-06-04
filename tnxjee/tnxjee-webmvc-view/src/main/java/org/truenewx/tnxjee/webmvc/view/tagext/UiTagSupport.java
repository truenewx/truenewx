package org.truenewx.tnxjee.webmvc.view.tagext;

import javax.servlet.jsp.JspException;

/**
 * UI标签支持
 *
 * @author jianglei
 */
public class UiTagSupport extends SimpleDynamicAttributeTagSupport {

    public void setId(String id) throws JspException {
        setDynamicAttribute(null, "id", id);
    }

    public void setName(String name) throws JspException {
        setDynamicAttribute(null, "name", name);
    }

    public void setClassName(String className) throws JspException {
        setDynamicAttribute(null, "class", className);
    }

    public void setStyle(String style) throws JspException {
        setDynamicAttribute(null, "style", style);
    }

    public void setTitle(String title) throws JspException {
        setDynamicAttribute(null, "title", title);
    }

    public void setDisabled(String disabled) throws JspException {
        setDynamicAttribute(null, "disabled", disabled);
    }

    public void setTabIndex(String tabIndex) throws JspException {
        setDynamicAttribute(null, "tabIndex", tabIndex);
    }

    public void setPlaceholder(String placeholder) throws JspException {
        setDynamicAttribute(null, "placeholder", placeholder);
    }

    public void setOnclick(String onclick) throws JspException {
        setDynamicAttribute(null, "onclick", onclick);
    }

    public void setOndblclick(String ondblclick) throws JspException {
        setDynamicAttribute(null, "ondblclick", ondblclick);
    }

    public void setOnmousedown(String onmousedown) throws JspException {
        setDynamicAttribute(null, "onmousedown", onmousedown);
    }

    public void setOnmouseup(String onmouseup) throws JspException {
        setDynamicAttribute(null, "onmouseup", onmouseup);
    }

    public void setOnmouseover(String onmouseover) throws JspException {
        setDynamicAttribute(null, "onmouseover", onmouseover);
    }

    public void setOnmousemove(String onmousemove) throws JspException {
        setDynamicAttribute(null, "onmousemove", onmousemove);
    }

    public void setOnmouseout(String onmouseout) throws JspException {
        setDynamicAttribute(null, "onmouseout", onmouseout);
    }

    public void setOnfocus(String onfocus) throws JspException {
        setDynamicAttribute(null, "onfocus", onfocus);
    }

    public void setOnblur(String onblur) throws JspException {
        setDynamicAttribute(null, "onblur", onblur);
    }

    public void setOnkeypress(String onkeypress) throws JspException {
        setDynamicAttribute(null, "onkeypress", onkeypress);
    }

    public void setOnkeydown(String onkeydown) throws JspException {
        setDynamicAttribute(null, "onkeydown", onkeydown);
    }

    public void setOnkeyup(String onkeyup) throws JspException {
        setDynamicAttribute(null, "onkeyup", onkeyup);
    }

    public void setOnselect(String onselect) throws JspException {
        setDynamicAttribute(null, "onselect", onselect);
    }

    public void setOnchange(String onchange) throws JspException {
        setDynamicAttribute(null, "onchange", onchange);
    }

    protected String getId() {
        return (String) this.attributes.get("id");
    }

    protected String getName() {
        return (String) this.attributes.get("name");
    }

}
