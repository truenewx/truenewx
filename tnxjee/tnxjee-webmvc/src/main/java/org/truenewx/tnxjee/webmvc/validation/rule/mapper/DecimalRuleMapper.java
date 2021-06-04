package org.truenewx.tnxjee.webmvc.validation.rule.mapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.validation.rule.DecimalRule;

/**
 * 数值范围的校验映射集生成器
 *
 * @author jianglei
 */
@Component
public class DecimalRuleMapper implements ValidationRuleMapper<DecimalRule> {

    @Override
    public Map<String, Object> toMap(DecimalRule rule, Locale locale) {
        Map<String, Object> result = new HashMap<>();
        result.put("number", Boolean.TRUE); // 至少必须为数值
        int precision = rule.getPrecision();
        int scale = rule.getScale();
        if (scale >= 0 && precision > scale) { // 精度大于等于0且长度大于精度才有效，不支持负精度
            if (scale == 0) { // 小数位精度为0，则限定为整数
                result.put("integer", Boolean.TRUE);
                result.put("maxLength", precision);
                result.remove("number");
            } else {
                result.put("integerLength", precision - scale);
                result.put("scale", scale);
            }
        }
        BigDecimal min = rule.getMin();
        if (min != null && min.compareTo(DecimalRule.MIN_DECIMAL) > 0) {
            result.put("minValue", min);
            result.put("inclusiveMin", rule.isInclusiveMin());
        }
        BigDecimal max = rule.getMax();
        if (max != null && max.compareTo(DecimalRule.MAX_DECIMAL) < 0) {
            result.put("maxValue", max);
            result.put("inclusiveMax", rule.isInclusiveMax());
        }
        return result;
    }

}
