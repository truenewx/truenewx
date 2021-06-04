package org.truenewx.tnxjeex.fss.web.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;
import org.truenewx.tnxjeex.fss.api.FssMetaResolver;

/**
 * 格式化日期输出标签
 *
 * @author jianglei
 */
public class FssReadUrlTag extends SimpleTagSupport {

    private String value;
    private boolean thumbnail;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void setValue(String value) {
        this.value = value;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    private FssMetaResolver getReadUrlResolver() {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(request);
        if (context != null) {
            return SpringUtil.getFirstBeanByClass(context, FssMetaResolver.class);
        }
        return null;
    }

    @Override
    public void doTag() throws JspException, IOException {
        FssMetaResolver metaResolver = getReadUrlResolver();
        if (metaResolver != null) {
            try {
                String readUrl = metaResolver.resolveReadUrl(this.value, this.thumbnail);
                if (readUrl != null) {
                    JspWriter out = getJspContext().getOut();
                    out.print(readUrl);
                }
            } catch (BusinessException e) {
                this.logger.error(e.getMessage(), e);
            }
        }
    }

}
