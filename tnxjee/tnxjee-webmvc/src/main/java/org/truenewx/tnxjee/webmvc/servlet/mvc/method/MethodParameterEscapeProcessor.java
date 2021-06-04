package org.truenewx.tnxjee.webmvc.servlet.mvc.method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.MethodParameter;
import org.springframework.web.util.HtmlUtils;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.annotation.Escaped;

/**
 * 方法参数转义处理器
 */
public class MethodParameterEscapeProcessor {

    public static final MethodParameterEscapeProcessor INSTANCE = new MethodParameterEscapeProcessor();

    private Map<Class<?>, Set<String>> beanPropertiesMapping = new HashMap<>();

    private MethodParameterEscapeProcessor() {
    }

    public boolean supports(MethodParameter parameter) {
        Class<?> type = parameter.getParameterType();
        return ClassUtil.isComplex(type) || (type == String.class
                && parameter.getParameterAnnotation(Escaped.class) != null);
    }

    public Object escape(Object object, boolean opposite) {
        if (object != null) {
            Class<?> clazz = object.getClass();
            if (ClassUtil.isComplex(clazz)) {
                Set<String> properties = this.beanPropertiesMapping.get(clazz);
                if (properties == null) {
                    Set<String> escapedProperties = new HashSet<>();
                    ClassUtil.loopFieldsByAnnotation(clazz, Escaped.class, (field, escaped) -> {
                        // 字符串类型且需HTML转义的字段才是有效的转义字段
                        Class<?> type = field.getType();
                        if (type == String.class) {
                            escapedProperties.add(field.getName());
                        } else if (ClassUtil.isComplex(type)) { // 复合类型递归转义处理
                            escape(BeanUtil.getPropertyValue(object, field.getName()), opposite);
                        }
                        return true;
                    });
                    properties = escapedProperties;
                    this.beanPropertiesMapping.put(clazz, properties);
                }
                for (String propertyName : properties) {
                    Object value = BeanUtil.getPropertyValue(object, propertyName);
                    if (value instanceof String) {
                        String escapedValue = opposite ? HtmlUtils.htmlUnescape((String) value)
                                : HtmlUtils.htmlEscape((String) value);
                        if (!value.equals(escapedValue)) {
                            BeanUtil.setPropertyValue(object, propertyName, escapedValue);
                        }
                    }
                }
            } else if (object instanceof String) {
                return opposite ? HtmlUtils.htmlUnescape((String) object)
                        : HtmlUtils.htmlEscape((String) object);
            }
        }
        return object;
    }

}
