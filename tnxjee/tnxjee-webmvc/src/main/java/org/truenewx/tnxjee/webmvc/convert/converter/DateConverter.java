package org.truenewx.tnxjee.webmvc.convert.converter;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        return new Date(Long.parseLong(source));
    }

}
