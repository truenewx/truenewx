package org.truenewx.tnxjee.repo.jpa.converter;

import java.math.BigDecimal;

import javax.persistence.Converter;

/**
 * BigDecimal范围属性转换器
 */
@Converter
public class BigDecimalRangeAttributeConverter extends NumberRangeAttributeConverter<BigDecimal> {
}
