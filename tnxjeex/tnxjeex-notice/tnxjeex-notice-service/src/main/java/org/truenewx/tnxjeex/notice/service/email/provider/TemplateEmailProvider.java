package org.truenewx.tnxjeex.notice.service.email.provider;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.parser.FreeMarkerTemplateParser;
import org.truenewx.tnxjee.core.parser.TemplateParser;

/**
 * 基于模板的邮件提供者
 *
 * @author jianglei
 */
public class TemplateEmailProvider extends AbstractEmailProvider {
    /**
     * 邮件模板资源
     */
    private Resource resource;

    /**
     * 模板解析器
     */
    private TemplateParser parser = new FreeMarkerTemplateParser();

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * 设置模板解析器
     *
     * @param parser 模板解析器
     */
    public void setParser(TemplateParser parser) {
        this.parser = parser;
    }

    @Override
    public String getTitle(Map<String, Object> params, Locale locale) {
        try {
            return this.parser.parse(this.title, params, locale);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
            return Strings.EMPTY;
        }
    }

    @Override
    public String getContent(Map<String, Object> params, Locale locale) {
        try {
            return this.parser.parse(this.resource.getFile(), params, locale);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
