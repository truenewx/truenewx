package org.truenewx.tnxjee.repo.jpa.converter;

import java.util.List;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.JacksonUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Object-JSON字符串的属性转换器
 *
 * @author jianglei
 */
@Converter
public class ObjectToJsonAttributeConverter implements AttributeConverter<Object, String> {

    private ObjectMapper mapper = JacksonUtil.withComplexClassProperty(JacksonUtil.copyDefaultMapper());

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        String json = null;
        if (attribute != null) {
            try {
                json = this.mapper.writeValueAsString(attribute);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return json;
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (StringUtils.isNotBlank(dbData)) {
            try {
                // 原则上，设计者应该避免同一个数据库字段对应的实体类属性可能既是对象又是集合的情况；
                // 如果确实无法避免，则至少需确保集合类型为List，否则此处无法做出正确转换，需自定义转换器
                if (dbData.startsWith(Strings.LEFT_SQUARE_BRACKET) && dbData.endsWith(Strings.RIGHT_SQUARE_BRACKET)) {
                    return this.mapper.readValue(dbData, List.class);
                } else {
                    Object value = this.mapper.readValue(dbData, Object.class);
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) value;
                        String className = (String) map.get(JacksonUtil.getTypePropertyName());
                        if (StringUtils.isNotBlank(className)) {
                            Class<?> clazz = Class.forName(className);
                            value = this.mapper.readValue(dbData, clazz);
                        }
                    }
                    return value;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
