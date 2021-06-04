package org.truenewx.tnxjee.repo.jpa.converter.spec;

import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.model.spec.user.DefaultUserIdentity;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 默认用户标识属性转换器
 */
@Converter
public class DefaultUserIdentityAttributeConverter extends IntegerUserIdentityAttributeConverter {

    @Override
    public UserIdentity<Integer> convertToEntityAttribute(String dbData) {
        return StringUtils.isBlank(dbData) ? null : DefaultUserIdentity.of(dbData);
    }

}
