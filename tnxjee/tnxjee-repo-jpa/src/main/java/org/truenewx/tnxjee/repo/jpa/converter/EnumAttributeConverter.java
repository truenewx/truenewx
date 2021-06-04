package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.spec.enums.support.EnumValueHelper;

/**
 * 枚举属性转换器
 *
 * @author jianglei
 */
@Converter
public abstract class EnumAttributeConverter<T extends Enum<T>> implements AttributeConverter<T, String> {

    private Class<T> enumType;

    protected Class<T> getEnumType() {
        if (this.enumType == null) {
            this.enumType = ClassUtil.getActualGenericType(getClass(), 0);
        }
        return this.enumType;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (attribute == null) {
            return null;
        }
        String value = EnumValueHelper.getValue(attribute);
        if (value == null) {
            value = attribute.name();
        }
        return value;
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        T attribute = EnumValueHelper.valueOf(getEnumType(), dbData);
        if (attribute == null) {
            attribute = EnumUtils.getEnum(getEnumType(), dbData);
        }
        return attribute;
    }

}
