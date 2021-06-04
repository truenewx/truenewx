package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 格式化输出标签
 *
 * @author jianglei
 */
public class FormatTag extends SimpleTagSupport {

    private Object value;
    private String pattern;

    public void setValue(Object value) {
        this.value = value;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void doTag() throws JspException, IOException {
        String result = null;
        if (this.value instanceof Date) {
            if (this.pattern == null) {
                this.pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = DateUtil.format((Date) this.value, this.pattern);
        } else if (this.value instanceof Instant) {
            if (this.pattern == null) {
                this.pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = TemporalUtil.format((Instant) this.value, this.pattern);
        } else if (this.value instanceof LocalDate) {
            if (this.pattern == null) {
                this.pattern = DateUtil.SHORT_DATE_PATTERN;
            }
            result = TemporalUtil.format((LocalDate) this.value, this.pattern);
        } else if (this.value instanceof LocalTime) {
            if (this.pattern == null) {
                this.pattern = DateUtil.TIME_PATTERN;
            }
            result = TemporalUtil.format((LocalTime) this.value, this.pattern);
        } else if (this.value instanceof LocalDateTime) {
            if (this.pattern == null) {
                this.pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = TemporalUtil.format((LocalDateTime) this.value, this.pattern);
        } else if (this.value instanceof Number) {
            if (this.pattern == null) {
                this.pattern = "0.##";
            }
            result = new DecimalFormat(this.pattern).format(this.value);
        }
        if (result != null) {
            JspWriter out = getJspContext().getOut();
            out.print(result);
        }
    }
}
