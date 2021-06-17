package org.truenewx.tnxjee.core.enums.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.tnxjee.core.Strings;

/**
 * 枚举类型的显示文本规则
 *
 * @author jianglei
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumTypeCaption {
    /**
     * @return 当前枚举的常量的显示名称均应该具有的前缀
     */
    String prefix() default Strings.EMPTY;

    /**
     * @return 当前枚举的常量的显示名称均应该具有的后缀
     */
    String suffix() default Strings.EMPTY;
}
