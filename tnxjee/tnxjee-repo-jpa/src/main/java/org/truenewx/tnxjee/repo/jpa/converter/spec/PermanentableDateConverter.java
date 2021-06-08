package org.truenewx.tnxjee.repo.jpa.converter.spec;

import java.util.Date;

import javax.persistence.AttributeConverter;

import org.truenewx.tnxjee.core.spec.PermanentableDate;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 可表示永久的日期属性转换器
 */
public class PermanentableDateConverter implements AttributeConverter<PermanentableDate, Date> {

    @Override
    public Date convertToDatabaseColumn(PermanentableDate attribute) {
        return attribute == null ? null : TemporalUtil.toDate(attribute.getValue());
    }

    @Override
    public PermanentableDate convertToEntityAttribute(Date dbData) {
        return dbData == null ? null : new PermanentableDate(TemporalUtil.toLocalDate(dbData.toInstant()));
    }

}
