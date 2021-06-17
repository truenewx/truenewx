package org.truenewx.tnxjee.core.enums.annotation;

import java.lang.annotation.*;

import org.truenewx.tnxjee.core.Strings;

/**
 * 标注属性是枚举项代码
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumItemKey {

    /**
     * @return 所属的枚举类型
     */
    String type();

    /**
     * @return 所属的枚举子类型
     */
    String subtype() default Strings.EMPTY;

}
