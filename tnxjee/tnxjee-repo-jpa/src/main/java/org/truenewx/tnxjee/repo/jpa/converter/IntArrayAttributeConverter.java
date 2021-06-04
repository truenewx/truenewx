package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;

/**
 * 整型数组属性转换器
 *
 * @author jianglei
 */
@Converter
public class IntArrayAttributeConverter implements AttributeConverter<int[], String> {

    @Override
    public String convertToDatabaseColumn(int[] attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.length == 0) {
            return Strings.EMPTY;
        }
        return StringUtils.join(attribute, ',');
    }

    @Override
    public int[] convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return MathUtil.parseIntArray(dbData, Strings.COMMA);
    }

}
