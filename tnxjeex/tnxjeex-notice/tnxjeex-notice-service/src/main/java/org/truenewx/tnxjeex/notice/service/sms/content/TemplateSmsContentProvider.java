package org.truenewx.tnxjeex.notice.service.sms.content;

import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSourceAware;
import org.truenewx.tnxjee.core.parser.SimpleElTemplateParser;
import org.truenewx.tnxjee.core.parser.TemplateParser;

/**
 * 基于模版的短信提供者
 *
 * @author jianglei
 */
public class TemplateSmsContentProvider extends AbstractSmsContentProvider implements MessageSourceAware {
    private String code;
    private TemplateParser parser = new SimpleElTemplateParser();

    public void setCode(String code) {
        this.code = code;
    }

    public void setParser(TemplateParser parser) {
        this.parser = parser;
    }

    @Override
    public String getContent(Map<String, Object> params, Locale locale) {
        String templateContent = this.messageSource.getMessage(this.code, null, locale);
        return this.parser.parse(templateContent, params, locale);
    }

}
