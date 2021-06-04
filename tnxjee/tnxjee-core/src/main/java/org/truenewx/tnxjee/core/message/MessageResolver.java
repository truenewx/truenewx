package org.truenewx.tnxjee.core.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;

/**
 * 消息解决器
 *
 * @author jianglei
 */
@Component
public class MessageResolver {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private EnumDictResolver enumDictResolver;

    public String resolveMessage(String code, Object... args) {
        return resolveMessage(code, null, args);
    }

    /**
     * 解析指定异常错误码得到异常消息
     *
     * @param code   异常错误码
     * @param locale 区域
     * @param args   异常参数
     * @return 异常消息
     */
    public String resolveMessage(String code, Locale locale, Object... args) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String[] argTexts = getArgTexts(args, locale);
        return this.messageSource.getMessage(code, argTexts, code, locale);
    }

    private String[] getArgTexts(Object[] args, Locale locale) {
        if (args == null) {
            return null;
        }
        String[] result = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Enum<?>) {
                result[i] = this.enumDictResolver.getText((Enum<?>) arg, locale);
            } else if (arg != null) {
                result[i] = arg.toString();
            }
            if (result[i] == null) {
                result[i] = Strings.EMPTY;
            }
        }
        return result;
    }

}
