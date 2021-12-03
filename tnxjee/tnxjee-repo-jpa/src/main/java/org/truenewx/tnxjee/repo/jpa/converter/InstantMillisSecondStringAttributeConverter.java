package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.Converter;

@Converter
public class InstantMillisSecondStringAttributeConverter extends InstantStringAttributeConverter {

    public InstantMillisSecondStringAttributeConverter(boolean zoned) {
        super(3, zoned);
    }

    public InstantMillisSecondStringAttributeConverter() {
        this(false);
    }

}
