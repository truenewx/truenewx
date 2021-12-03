package org.truenewx.tnxjee.repo.jpa.converter;

import javax.persistence.Converter;

@Converter
public class InstantZonedMillisSecondStringAttributeConverter extends InstantMillisSecondStringAttributeConverter {

    public InstantZonedMillisSecondStringAttributeConverter() {
        super(true);
    }

}
