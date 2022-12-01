package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.ExceptionUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;

/**
 * 字符串数组-JSON字符串的属性转换器
 *
 * @author jianglei
 */
@Converter
public class StringArrayToJsonAttributeConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        String json = null;
        if (attribute != null) {
            try {
                json = JsonUtil.toJson(attribute);
            } catch (Exception e) {
                throw ExceptionUtil.toRuntimeException(e);
            }
        }
        return json;
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        if (StringUtils.isNotBlank(dbData)) {
            try {
                return JsonUtil.json2Array(dbData, String.class);
            } catch (Exception e) {
                throw ExceptionUtil.toRuntimeException(e);
            }
        }
        return null;
    }

}
