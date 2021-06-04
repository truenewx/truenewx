package org.truenewx.tnxjee.webmvc.convert.converter;

import java.time.LocalDate;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.TemporalUtil;

@Component
public class LocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        return TemporalUtil.parseDate(source);
    }

}
