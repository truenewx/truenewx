package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.slf4j.LoggerFactory;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.webmvc.view.tagext.SimpleDynamicAttributeTagSupport;

/**
 * 将值转换为JSON字符串的标签
 *
 * @author jianglei
 */
public class ToJsonTag extends SimpleDynamicAttributeTagSupport {
    private Object value;
    private boolean toSingleQuote = true;
    private Map<String, Object> defaultValues;
    private Map<String, Object> extendValues;

    public void setValue(Object value) {
        this.value = value;
    }

    public void setToSingleQuote(boolean toSingleQuote) {
        this.toSingleQuote = toSingleQuote;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parse(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        } else if (value instanceof String) {
            return JsonUtil.json2Map((String) value);
        } else if (value != null && ClassUtil.isComplex(value.getClass())) {
            return BeanUtil.toMap(value);
        }
        return null;
    }

    public void setDefault(Object defaultValue) {
        this.defaultValues = parse(defaultValue);
    }

    public void setExtend(Object extend) {
        this.extendValues = parse(extend);
    }

    @Override
    public void doTag() throws JspException, IOException {
        if ((this.defaultValues == null || this.defaultValues.isEmpty())
                && (this.extendValues == null || this.extendValues.isEmpty())) { // 无默认值和扩展值，则仅序列化取值
            if (this.value != null) {
                try {
                    String json = JsonUtil.toJson(this.value);
                    if (this.toSingleQuote) {
                        // 转换双引号为单引号，使在页面字符串中不与双引号冲突
                        json = json.replace('\"', '\'');
                    }
                    print(json);
                } catch (Exception e) {
                    LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                    // 出现异常不打印任何字符
                }
            }
        } else { // 有默认值或扩展值，则先后叠加后再序列化
            Map<String, Object> map = new HashMap<>();
            if (this.defaultValues != null) {
                map.putAll(this.defaultValues);
            }
            if (this.value != null) {
                Map<String, Object> values = parse(this.value);
                if (values != null) {
                    map.putAll(values);
                }
            }
            if (this.extendValues != null) {
                map.putAll(this.extendValues);
            }
            try {
                String json = JsonUtil.toJson(map);
                if (this.toSingleQuote) {
                    // 转换双引号为单引号，使在页面字符串中不与双引号冲突
                    json = json.replace('\"', '\'');
                }
                print(json);
            } catch (Exception e) {
                LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            }
        }
    }

}
