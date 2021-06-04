package org.truenewx.tnxjee.core.i18n;

import java.util.Locale;

/**
 * 国际化显示文本解析器
 *
 * @author jianglei
 * 
 */
public interface TextResolver {

    String getText(String type, String subtype, String key, Locale locale, String... keys);

    String getText(String type, String key, Locale locale, String... keys);

    String getText(Enum<?> enumConstant, Locale locale);

}