package org.truenewx.tnxjee.core.enums.annotation;

import java.lang.annotation.*;

/**
 * 枚举子集
 *
 * @author jianglei
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(EnumSubs.class)
public @interface EnumSub {

    int DEFAULT_ORDINAL = -1;

    /**
     * @return 所属的枚举子集
     */
    String value();

    /**
     * @return 在指定枚举子集中的序号，大于等于0才有效，小于0时使用枚举常量的定义顺序
     */
    int ordinal() default DEFAULT_ORDINAL;

}
