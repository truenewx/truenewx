package org.truenewx.tnxjee.repo.jpa.converter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * Set-JSON字符串的属性转换器
 *
 * @author jianglei
 */
@Converter
public class SetToJsonAttributeConverter implements AttributeConverter<Set<?>, String> {

    protected Class<?> getComponentType() {
        return null;
    }

    protected String[] getExcludeProperties() {
        return null;
    }

    @Override
    public String convertToDatabaseColumn(Set<?> attribute) {
        String json = null;
        if (attribute != null) {
            Class<?> componentType = getComponentType();
            String[] excludeProperties = getExcludeProperties();
            if (componentType != null && ArrayUtils.isNotEmpty(excludeProperties)) {
                json = JsonUtil.toJson(attribute, componentType, excludeProperties);
            } else {
                json = JsonUtil.toJson(attribute);
            }
        }
        return json;
    }

    @Override
    public Set<?> convertToEntityAttribute(String dbData) {
        if (StringUtils.isNotBlank(dbData)) {
            if ("[]".equals(dbData)) {
                return Collections.emptySet();
            }
            try {
                List<?> list;
                Class<?> componentType = getComponentType();
                if (componentType == null) {
                    list = JsonUtil.json2List(dbData);
                } else {
                    list = JsonUtil.json2List(dbData, componentType);
                }
                return new HashSet<>(list);
            } catch (Exception e) {
                LogUtil.error(getClass(), e);
            }
        }
        return null;
    }

}
