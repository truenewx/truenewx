package org.truenewx.tnxjee.webmvc.validation.rule.mapper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.validation.rule.HtmlTagLimitRule;

/**
 * Html标签限定规则映射生成器
 *
 * @author jianglei
 */
@Component
public class HtmlTagLimitRuleMapper implements ValidationRuleMapper<HtmlTagLimitRule> {

    @Override
    public Map<String, Object> toMap(HtmlTagLimitRule rule, Locale locale) {
        Map<String, Object> result = new HashMap<>();
        Set<String> allowed = rule.getAllowed();
        Set<String> forbidden = rule.getForbidden();
        if (allowed.isEmpty() && forbidden.isEmpty()) { // 不允许所有标签
            result.put("rejectTags", Boolean.TRUE);
        } else {
            if (allowed.size() > 0) { // 存在仅允许的标签
                result.put("allowedTags", StringUtils.join(allowed, Strings.COMMA));
            }
            if (forbidden.size() > 0) { // 存在禁止的标签
                result.put("forbiddenTags", StringUtils.join(forbidden, Strings.COMMA));
            }
        }
        return result;
    }

}
