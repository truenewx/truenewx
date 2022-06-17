package org.truenewx.tnxjee.core.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * 简单EL表达式模板的解析器。以简单方式解析模板内容中的EL表达式，生成实际内容
 *
 * @author jianglei
 */
public class SimpleElTemplateParser implements TemplateParser {

    private static final String REPLACE_KEY_PREFIX = "${";
    private static final String REPLACE_KEY_SUFFIX = "}";

    @Override
    public String parse(String templateContent, Map<String, Object> params,
            Locale locale) {
        if (StringUtils.isEmpty(templateContent)) {
            return templateContent;
        }
        if (params == null) {
            params = new HashMap<>(0);
        }
        String[] replaceContents = StringUtil.substringsBetween(templateContent,
                REPLACE_KEY_PREFIX, REPLACE_KEY_SUFFIX);
        for (String replaceContent : replaceContents) {
            try {
                String replaceKey = replaceContent.substring(REPLACE_KEY_PREFIX.length(),
                        replaceContent.length() - REPLACE_KEY_SUFFIX.length());
                if (StringUtils.isEmpty(replaceKey)) {
                    continue;
                }
                String[] propertyNames = replaceKey.split("\\.");
                if (propertyNames.length == 0) {
                    continue;
                }
                Object value = params.get(propertyNames[0]);
                if (propertyNames.length > 1) {
                    value = BeanUtil.getPropertyValue(value,
                            replaceKey.substring(propertyNames[0].length() + 1));
                } else {
                    String[] replaces = StringUtil.substringsBetween(value.toString(),
                            REPLACE_KEY_PREFIX, REPLACE_KEY_SUFFIX);
                    if (replaces.length > 0) {
                        value = parse(value.toString(), params, locale);
                    }
                }
                String key = REPLACE_KEY_PREFIX + replaceKey + REPLACE_KEY_SUFFIX;
                templateContent = templateContent.replace(key,
                        value == null ? "" : value.toString());
            } catch (Exception e) { // 忽略单个替换异常
                LogUtil.error(getClass(), e);
            }
        }
        return templateContent;
    }

    @Override
    public String parse(File templateFile, Map<String, Object> params, Locale locale)
            throws IOException {
        String templateContent = IOUtils.toString(new FileInputStream(templateFile),
                Strings.ENCODING_UTF8);
        return parse(templateContent, params, locale);
    }

}
