package org.truenewx.tnxjee.repo.jpa.converter;

import java.time.Instant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Instant-毫秒数的属性转换器
 */
@Converter
public class InstantMillisSecondNumberAttributeConverter implements AttributeConverter<Instant, Long> {

    @Override
    public Long convertToDatabaseColumn(Instant attribute) {
        if (attribute != null) {
            return attribute.toEpochMilli();
        }
        return null;
    }

    @Override
    public Instant convertToEntityAttribute(Long dbData) {
        if (dbData != null) {
            return Instant.ofEpochMilli(dbData);
        }
        return null;
    }

}
