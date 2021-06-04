package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;

/**
 * 长整型数组属性转换器
 *
 * @author jianglei
 */
@Converter
public class LongArrayAttributeConverter implements AttributeConverter<long[], String> {

    @Override
    public String convertToDatabaseColumn(long[] attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.length == 0) {
            return Strings.EMPTY;
        }
        return StringUtils.join(attribute, ',');
    }

    @Override
    public long[] convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return MathUtil.parseLongArray(dbData, Strings.COMMA);
    }

}
