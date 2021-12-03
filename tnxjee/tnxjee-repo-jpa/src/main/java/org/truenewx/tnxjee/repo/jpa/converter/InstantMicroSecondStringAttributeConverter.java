package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.Converter;

@Converter
public class InstantMicroSecondStringAttributeConverter extends InstantStringAttributeConverter {

    public InstantMicroSecondStringAttributeConverter(boolean zoned) {
        super(6, zoned);
    }

    public InstantMicroSecondStringAttributeConverter() {
        this(false);
    }

}
