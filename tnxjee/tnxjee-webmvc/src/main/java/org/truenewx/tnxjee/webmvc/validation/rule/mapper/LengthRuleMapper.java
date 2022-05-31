package org.truenewx.tnxjee.webmvc.validation.rule.mapper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.validation.rule.LengthRule;

/**
 * 字符串长度范围的校验映射集生成器
 *
 * @author jianglei
 */
@Component
public class LengthRuleMapper implements ValidationRuleMapper<LengthRule> {

    @Override
    public Map<String, Object> toMap(LengthRule rule, Locale locale) {
        Map<String, Object> result = new HashMap<>();
        Integer min = rule.getMin();
        if (min != null && min > 0) {
            result.put("minLength", min);
        }
        Integer max = rule.getMax();
        if (max != null && max < Integer.MAX_VALUE) {
            result.put("maxLength", max);
        }
        return result;
    }

}
