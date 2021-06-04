package org.truenewx.tnxjee.webmvc.convert.converter;

import java.time.Instant;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InstantConverter implements Converter<String, Instant> {

    @Override
    public Instant convert(String source) {
        return Instant.ofEpochMilli(Long.parseLong(source));
    }

}
