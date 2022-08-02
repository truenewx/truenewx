package org.truenewx.tnxjee.core.format;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.truenewx.tnxjee.core.spec.PermanentableDate;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 可表示永久的日期格式化器
 */
public class PermanentableDateFormatter {

    private String code;
    @Autowired
    private MessageSource messageSource;

    public PermanentableDateFormatter(String code) {
        this.code = code;
    }

    public PermanentableDateFormatter() {
        this("tnxjee.core.spec.permanentable_date.permanent");
    }

    public String format(PermanentableDate date, Locale locale) {
        if (date.isPermanent()) {
            return this.messageSource.getMessage(this.code, null, locale);
        } else {
            return TemporalUtil.format(date.getValue());
        }
    }

}
