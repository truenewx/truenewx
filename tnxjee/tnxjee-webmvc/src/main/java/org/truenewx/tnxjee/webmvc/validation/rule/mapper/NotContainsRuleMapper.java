package org.truenewx.tnxjee.webmvc.validation.rule.mapper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.validation.rule.NotContainsRule;

/**
 * 不能包含字符串规则映射生成器
 *
 * @author jianglei
 */
@Component
public class NotContainsRuleMapper implements ValidationRuleMapper<NotContainsRule> {

    @Override
    public Map<String, Object> toMap(NotContainsRule rule, Locale locale) {
        Map<String, Object> result = new HashMap<>();
        if (rule.hasValue()) { // 存在不能包含的字符串
            String notString = StringUtils.join(rule.getValues(), Strings.SPACE);
            result.put("notContains", HtmlUtils.htmlEscape(notString));
        }
        if (rule.isNotContainsHtmlChars()) {
            result.put("notContainsHtmlChars", Boolean.TRUE);
        } else if (rule.isNotContainsAngleBracket()) { // HTML字符包含了尖括号，仅在不限制HTML字符时才检查尖括号限制
            result.put("notContainsAngleBracket", Boolean.TRUE);
        }
        return result;
    }
}
