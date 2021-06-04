package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.slf4j.LoggerFactory;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.parser.FreeMarkerHelper;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.webmvc.view.tagext.SimpleDynamicAttributeTagSupport;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 通过FreeMarker加载内容的标签
 *
 * @author jianglei
 */
public class FtlTag extends SimpleDynamicAttributeTagSupport {

    private Template template;

    public void setBasePath(String basePath) throws IOException {
        // 确保以/开头
        if (!basePath.startsWith(Strings.SLASH)) {
            basePath = Strings.SLASH + basePath;
        }
        // 确保不包含.ftl扩展名
        if (basePath.toLowerCase().endsWith(".ftl")) {
            basePath = basePath.substring(0, basePath.length() - 4);
        }
        int index = basePath.lastIndexOf(Strings.SLASH);
        String baseDir = basePath.substring(0, index + 1);
        baseDir = getPageContext().getServletContext().getRealPath(baseDir);
        String baseName = basePath.substring(index + 1);
        Locale locale = getLocale();
        File file = IOUtil.findI18nFileByDir(baseDir, baseName, "ftl", locale);
        if (file != null) {
            Configuration config = FreeMarkerHelper.getDefaultConfiguration();
            config.setDirectoryForTemplateLoading(file.getParentFile());
            this.template = config.getTemplate(file.getName(), locale);
        }
    }

    @Override
    public void doTag() throws JspException, IOException {
        process(this.attributes);
    }

    protected void process(Map<String, Object> params) throws IOException {
        try {
            this.template.process(params, getJspContext().getOut());
        } catch (TemplateException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }
}
