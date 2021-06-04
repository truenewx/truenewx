package org.truenewx.tnxjee.repo.jpa.converter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.spec.enums.support.EnumValueHelper;

/**
 * 枚举数组属性转换器
 *
 * @param <T> 枚举类型
 */
@Converter
public abstract class EnumArrayAttributeConverter<T extends Enum<T>> implements AttributeConverter<T[], String> {

    private Class<T> enumType;

    protected Class<T> getEnumType() {
        if (this.enumType == null) {
            this.enumType = ClassUtil.getActualGenericType(getClass(), 0);
        }
        return this.enumType;
    }

    @Override
    public String convertToDatabaseColumn(T[] attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.length == 0) {
            return Strings.EMPTY;
        }
        List<String> list = new ArrayList<>();
        for (T attr : attribute) {
            String value = EnumValueHelper.getValue(attr);
            if (value == null) {
                value = attr.name();
            }
            list.add(value);
        }
        return StringUtils.join(list, Strings.COMMA);
    }

    @Override
    public T[] convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(getEnumType(), 0);
        if (StringUtils.isBlank(dbData)) {
            return array;
        }
        List<T> list = new ArrayList<>();
        String[] dataArray = dbData.split(Strings.COMMA);
        for (String data : dataArray) {
            T attr = EnumValueHelper.valueOf(getEnumType(), data);
            if (attr == null) {
                attr = EnumUtils.getEnum(getEnumType(), data);
            }
            list.add(attr);
        }
        return list.toArray(array);
    }

}
