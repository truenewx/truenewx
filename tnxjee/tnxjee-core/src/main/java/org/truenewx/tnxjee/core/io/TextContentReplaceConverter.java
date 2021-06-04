package org.truenewx.tnxjee.core.io;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 文本内容替换转换器
 *
 * @author jianglei
 * 
 */
public class TextContentReplaceConverter implements TextContentConverter {

    private Map<String, String> replacement;

    public TextContentReplaceConverter(Map<String, String> replacement) {
        this.replacement = replacement;
    }

    @Override
    public String convert(String content) {
        for (Entry<String, String> entry : this.replacement.entrySet()) {
            content = content.replaceAll(entry.getKey(), entry.getValue());
        }
        return content;
    }

}
