package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.service.spec.region.Region;
import org.truenewx.tnxjee.service.spec.region.RegionSource;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * 多级区划显示标签
 *
 * @author jianglei
 */
public class RegionsTag extends TagSupport {

    private static final long serialVersionUID = 7264526360901106055L;

    /**
     * 行政区划代号
     */
    private String value;

    /**
     * 分隔符
     */
    private String delimiter = Strings.EMPTY;

    /**
     * 起始层级
     */
    private int startLevel = 2;

    /**
     * 结束层级
     */
    private int endLevel = 4;

    /**
     * @param delimiter 分隔符
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @param value 行政区划代号
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @param startLevel 起始层级
     */
    public void setStartLevel(int startLevel) {
        this.startLevel = startLevel;
    }

    /**
     * @param endLevel 结束层级
     */
    public void setEndLevel(int endLevel) {
        this.endLevel = endLevel;
    }

    private Locale getLocale() {
        HttpServletRequest request = getRequest();
        return SpringWebMvcUtil.getLocale(request);
    }

    private HttpServletRequest getRequest() {
        return (HttpServletRequest) this.pageContext.getRequest();
    }

    private String appendCaptions() {
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(getRequest());
        RegionSource regionSource = SpringUtil.getFirstBeanByClass(context, RegionSource.class);
        Region region = regionSource.getRegion(this.value, getLocale());

        List<String> captions = new ArrayList<>();
        StringBuffer caption = new StringBuffer();
        if (region != null) {
            Region parent = region.getParent();
            captions.add(region.getCaption());
            while (parent != null) {
                captions.add(0, parent.getCaption());
                parent = parent.getParent();
            }
            for (int i = this.startLevel - 1; i < captions.size(); i++) {
                caption.append(captions.get(i)).append(this.delimiter);
                if (i >= this.endLevel - 1) {
                    break;
                }
            }
            if (caption.length() > 0) {
                caption.delete(caption.length() - this.delimiter.length(), caption.length());
            }
        }
        return caption.toString();
    }

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        try {
            out.print(appendCaptions());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }
}
