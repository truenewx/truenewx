package org.truenewx.tnxjee.core.format;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.spec.PermanentableDate;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 可表示永久的日期格式化器
 */
@Component
public class PermanentableDateFormatter {

    private String code = "tnxjee.core.spec.permanentable_date.permanent";
    @Autowired
    private MessageSource messageSource;

    public void setCode(String code) {
        this.code = code;
    }

    public String format(PermanentableDate date, Locale locale) {
        if (date.isPermanent()) {
            return this.messageSource.getMessage(this.code, null, locale);
        } else {
            return TemporalUtil.format(date.getValue());
        }
    }

}
