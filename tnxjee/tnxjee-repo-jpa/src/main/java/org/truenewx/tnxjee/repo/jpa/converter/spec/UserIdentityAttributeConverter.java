package org.truenewx.tnxjee.repo.jpa.converter.spec;

import java.io.Serializable;

import javax.persistence.AttributeConverter;

import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 用户标识属性转换器
 *
 * @param <K> 标识类型
 */
public abstract class UserIdentityAttributeConverter<K extends Serializable>
        implements AttributeConverter<UserIdentity<K>, String> {

    @Override
    public String convertToDatabaseColumn(UserIdentity<K> attribute) {
        return attribute == null ? null : attribute.toString();
    }

}
