package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.Converter;

@Converter
public class InstantZonedMicroSecondStringAttributeConverter extends InstantMicroSecondStringAttributeConverter {

    public InstantZonedMicroSecondStringAttributeConverter() {
        super(true);
    }

}
