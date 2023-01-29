package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.core.util.OSUtil;
import org.truenewx.tnxjee.model.annotation.EnumValue;

/**
 * 操作系统类型
 *
 * @author jianglei
 */
public enum OS {

    @Caption("Windows")
    @EnumValue("W")
    WINDOWS,

    @Caption("安卓")
    @EnumValue("A")
    ANDROID,

    @Caption("苹果")
    @EnumValue("M")
    MAC,

    @Caption("Linux")
    @EnumValue("X")
    LINUX,

    @Caption("所有")
    @EnumValue("L")
    ALL;

    public static OS current() {
        String os = OSUtil.currentSystem();
        switch (os) {
            case Strings.OS_WINDOWS:
                return WINDOWS;
            case Strings.OS_ANDROID:
                return ANDROID;
            case Strings.OS_IOS:
            case Strings.OS_MAC:
                return MAC;
            default:
                return LINUX;
        }
    }

}
