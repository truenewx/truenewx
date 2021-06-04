package org.truenewx.tnxjee.webmvc.validation.rule.mapper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.model.validation.rule.RegexRule;

/**
 * 正则表达式规则的校验映射集生成器
 *
 * @author jianglei
 */
@Component
public class RegexRuleMapper implements ValidationRuleMapper<RegexRule>, MessageSourceAware {
    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Map<String, Object> toMap(RegexRule rule, Locale locale) {
        String message = rule.getMessage();
        if (StringUtils.isNotBlank(message) && message.startsWith("{") && message.endsWith("}")) {
            String code = message.substring(1, message.length() - 1);
            message = null;
            if (code.startsWith("javax.validation.")) { // 对于官方标准校验规则的消息，先尝试从自定义消息中获取
                String localCode = Model.class.getPackageName() + code.substring(5);
                message = getMessage(localCode, locale);
            }
            if (message == null) {
                message = getMessage(code, locale);
            }
        }
        if (message == null) {
            message = Strings.EMPTY;
        } else {
            message = HtmlUtils.htmlEscape(message);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("regex", new String[]{ rule.getExpression(), message });
        return result;
    }

    private String getMessage(String code, Locale locale) {
        String message = this.messageSource.getMessage(code, null, null, locale);
        if (code.equals(message)) {
            message = null;
        }
        return message;
    }

}
