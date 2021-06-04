package org.truenewx.tnxjee.model.spec.enums;

import org.truenewx.tnxjee.core.caption.Caption;
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
        String name = System.getProperty("os.name").toUpperCase();
        if (name.contains(WINDOWS.name())) {
            return WINDOWS;
        } else if (name.contains(ANDROID.name())) {
            return ANDROID;
        } else if (name.contains(MAC.name()) || name.contains("IOS")) {
            return MAC;
        }
        return LINUX;
    }

}
