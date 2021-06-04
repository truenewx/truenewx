package org.truenewx.tnxsample.root.model.entity;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 性别
 *
 * @author jianglei
 */
@Caption("性别")
public enum Gender {

    @EnumValue("0")
    @Caption("女")
    FEMALE,

    @Caption("男")
    @EnumValue("1")
    MALE,

    @EnumValue("9")
    @Caption("其他")
    OTHER;

}
