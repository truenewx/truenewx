package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;

import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.tuple.NumberRange;

/**
 * 数值范围属性转换器
 *
 * @param <T> 数值类型
 */
public abstract class NumberRangeConverter<T extends Number> implements AttributeConverter<NumberRange<T>, String> {

    @Override
    public String convertToDatabaseColumn(NumberRange<T> attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public NumberRange<T> convertToEntityAttribute(String dbData) {
        return NumberRange.parse(dbData, getNumberType());
    }

    protected Class<T> getNumberType() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

}
