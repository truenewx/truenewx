package org.truenewx.tnxjee.webmvc.convert.converter;

import java.time.LocalTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;

@Component
public class LocalTimeConverter implements Converter<String, LocalTime> {

    @Override
    public LocalTime convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        if (source.length() == DateUtil.TIME_PATTERN_TO_MINUTE.length()) {
            return TemporalUtil.parse(LocalTime.class, source, DateUtil.TIME_PATTERN_TO_MINUTE);
        }
        return TemporalUtil.parseTime(source);
    }

}
