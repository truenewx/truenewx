package org.truenewx.tnxjee.webmvc.view.tagext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * 简单动态属性标签支持
 *
 * @author jianglei
 */
public class SimpleDynamicAttributeTagSupport extends SimpleTagSupport
        implements DynamicAttributes {
    /**
     * 属性名-值映射集
     */
    protected final Map<String, Object> attributes = new HashMap<>();

    @Override
    public void setDynamicAttribute(String uri, String localName, Object value)
            throws JspException {
        if (value != null) {
            this.attributes.put(localName, value);
        }
    }

    /**
     * 将属性拼成属性串
     *
     * @param ignoredAttributes 忽略的属性
     * @return 属性串
     */
    protected final String joinAttributes(String... ignoredAttributes) {
        StringBuffer sb = new StringBuffer();
        for (Entry<String, Object> entry : this.attributes.entrySet()) {
            String name = entry.getKey();
            if (!ArrayUtils.contains(ignoredAttributes, name)) {
                sb.append(Strings.SPACE).append(name).append(Strings.EQUAL)
                        .append(Strings.DOUBLE_QUOTES).append(entry.getValue())
                        .append(Strings.DOUBLE_QUOTES);
            }
        }
        return sb.toString();
    }

    protected final PageContext getPageContext() {
        return (PageContext) getJspContext();
    }

    protected final HttpServletRequest getRequest() {
        return (HttpServletRequest) getPageContext().getRequest();
    }

    protected final Locale getLocale() {
        return getRequest().getLocale();
    }

    /**
     * 从Spring上下文容器中获取指定类型的bean对象
     *
     * @param beanClass bean类型
     * @return bean对象
     */
    protected final <T> T getBeanFromApplicationContext(Class<T> beanClass) {
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(getRequest());
        if (context != null) {
            return SpringUtil.getFirstBeanByClass(context, beanClass);
        }
        return null;
    }

    protected final String getMessage(String code, Object... args) {
        MessageSource messageSource = getBeanFromApplicationContext(MessageSource.class);
        return messageSource.getMessage(code, args, code, getLocale());
    }

    /**
     * 向响应中打印指定值集
     *
     * @param values 值集
     * @throws IOException 如果出现输出错误
     */
    protected final void print(Object... values) throws IOException {
        if (values != null) {
            JspWriter writer = getJspContext().getOut();
            for (Object value : values) {
                writer.print(value);
            }
        }
    }

}
