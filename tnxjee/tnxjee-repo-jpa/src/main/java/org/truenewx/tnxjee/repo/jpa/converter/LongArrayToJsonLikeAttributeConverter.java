package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.entity.util.EntityUtil;

/**
 * 长整型数组-类json字符串的属性转换器
 *
 * @author jianglei
 */
@Converter
public class LongArrayToJsonLikeAttributeConverter implements AttributeConverter<long[], String> {

    @Override
    public String convertToDatabaseColumn(long[] attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.length == 0) {
            return Strings.EMPTY;
        }
        return EntityUtil.toJsonLike(attribute);
    }

    @Override
    public long[] convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData.length() == 0) {
            return new long[0];
        }
        return EntityUtil.parseJsonLike(dbData, long[].class);
    }

}
