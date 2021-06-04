package org.truenewx.tnxjee.webmvc.view.enums.tag;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTag;
import javax.servlet.jsp.tagext.IterationTag;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.core.ForEachSupport;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumType;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * 枚举类型遍历标签
 *
 * @author jianglei
 */
public class EnumForEachTag extends ForEachSupport implements LoopTag, IterationTag {

    private static final long serialVersionUID = -798971033318943907L;

    private String type;
    private String subtype;

    public void setType(String type) throws JspException {
        this.type = getElExpressionValue("type", type, String.class);
    }

    public void setSubtype(String subtype) throws JspException {
        this.subtype = getElExpressionValue("subtype", subtype, String.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T getElExpressionValue(String attributeName, String expression, Class<T> expectedType)
            throws JspException {
        return (T) ExpressionEvaluatorManager.evaluate(attributeName, expression, expectedType, this.pageContext);
    }

    @Override
    protected void prepare() throws JspTagException {
        EnumDictResolver enumDictResolver = getBeanFromApplicationContext(EnumDictResolver.class);
        EnumType enumType = enumDictResolver.getEnumType(this.type, this.subtype, getLocale());
        if (enumType != null) {
            this.rawItems = enumType.getItems();
        }
        super.prepare();
    }

    private <T> T getBeanFromApplicationContext(Class<T> beanClass) {
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(request);
        if (context != null) {
            return SpringUtil.getFirstBeanByClass(context, beanClass);
        }
        return null;
    }

    private Locale getLocale() {
        return this.pageContext.getRequest().getLocale();
    }

}
