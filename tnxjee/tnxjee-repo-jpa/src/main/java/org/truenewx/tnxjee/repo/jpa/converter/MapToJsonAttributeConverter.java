package org.truenewx.tnxjee.repo.jpa.converter;

import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.ExceptionUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;

/**
 * Map-JSON字符串的属性转换器
 *
 * @author jianglei
 */
@Converter
public class MapToJsonAttributeConverter implements AttributeConverter<Map<String, Object>, String> {

    protected String[] getExcludeProperties() {
        return null;
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        String json = null;
        if (attribute != null) {
            String[] excludeProperties = getExcludeProperties();
            if (excludeProperties != null) {
                json = JsonUtil.toJson(attribute, excludeProperties);
            } else {
                json = JsonUtil.toJson(attribute);
            }
        }
        return json;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (StringUtils.isNotBlank(dbData)) {
            try {
                return JsonUtil.json2Map(dbData);
            } catch (Exception e) {
                throw ExceptionUtil.toRuntimeException(e);
            }
        }
        return null;
    }

}
