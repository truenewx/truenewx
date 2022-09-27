package org.truenewx.tnxjeex.notice.service.email.provider;

import java.util.Locale;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.parser.FreeMarkerTemplateParser;
import org.truenewx.tnxjee.core.parser.TemplateParser;
import org.truenewx.tnxjee.core.util.NetUtil;

/**
 * 基于URL的邮件提供者
 *
 * @author jianglei
 */
public class UrlEmailProvider extends AbstractEmailProvider {

    private String url;
    /**
     * 模板解析器
     */
    private TemplateParser parser = new FreeMarkerTemplateParser();

    public void setUrl(String url) {
        this.url = url;
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
        if (this.parser != null) {
            try {
                return this.parser.parse(this.title, params, locale);
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);
                return Strings.EMPTY;
            }
        } else {
            return this.title;
        }
    }

    @Override
    public String getContent(Map<String, Object> params, Locale locale) {
        try {
            return NetUtil.requestByPost(this.url, params, null);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

}
