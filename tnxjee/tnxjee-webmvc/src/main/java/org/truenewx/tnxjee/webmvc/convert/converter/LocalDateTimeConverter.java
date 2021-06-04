package org.truenewx.tnxjee.webmvc.convert.converter;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;

@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        LocalDateTime dateTime = TemporalUtil.parseDateTime(source);
        if (dateTime == null && source.length() == DateUtil.TIME_PATTERN_TO_MINUTE.length()) {
            dateTime = TemporalUtil.parse(LocalDateTime.class, source, DateUtil.TIME_PATTERN_TO_MINUTE);
        }
        return dateTime;
    }

}
