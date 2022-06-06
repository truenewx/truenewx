package org.truenewx.tnxjee.webmvc.view.tagext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * 动态属性标签支持
 *
 * @author jianglei
 */
public class DynamicAttributeTagSupport extends TagSupport implements DynamicAttributes {

    private final static long serialVersionUID = 7611853374776490358L;
    /**
     * 属性名-值映射集
     */
    protected Map<String, Object> dynamicAttributes = new HashMap<>();

    @Override
    public void setDynamicAttribute(String uri, String localName, Object value)
            throws JspException {
        if (value != null) {
            this.dynamicAttributes.put(localName, value);
        }
    }

    /**
     * 将动态属性拼成属性串
     *
     * @param ignoredAttributes 忽略的动态属性
     * @return 属性串
     */
    protected final String joinDynamicAttributes(String... ignoredAttributes) {
        StringBuffer sb = new StringBuffer();
        for (Entry<String, Object> entry : this.dynamicAttributes.entrySet()) {
            String name = entry.getKey();
            if (!ArrayUtils.contains(ignoredAttributes, name)) {
                sb.append(Strings.SPACE).append(name).append(Strings.EQUAL)
                        .append(Strings.DOUBLE_QUOTES).append(entry.getValue())
                        .append(Strings.DOUBLE_QUOTES);
            }
        }
        return sb.toString();
    }

    protected final Locale getLocale() {
        return getRequest().getLocale();
    }

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) this.pageContext.getRequest();
    }

    /**
     * 从Spring上下文容器中获取指定类型的bean对象
     *
     * @param beanClass bean类型
     * @param <T>       bean类型
     * @return bean对象
     */
    protected final <T> T getBeanFromApplicationContext(Class<T> beanClass) {
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(getRequest());
        if (context != null) {
            return SpringUtil.getFirstBeanByClass(context, beanClass);
        }
        return null;
    }

    /**
     * 向响应中打印指定值集
     *
     * @param values 值集
     * @throws IOException 如果出现输出错误
     */
    protected final void print(Object... values) throws IOException {
        if (values != null) {
            JspWriter writer = this.pageContext.getOut();
            for (Object value : values) {
                writer.print(value);
            }
        }
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            doTag();
        } catch (IOException e) {
            throw new JspException(e);
        }
        return super.doEndTag();
    }

    public void doTag() throws JspException, IOException {
    }

}
