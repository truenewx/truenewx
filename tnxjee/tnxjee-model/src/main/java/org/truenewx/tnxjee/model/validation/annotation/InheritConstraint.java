package org.truenewx.tnxjee.model.validation.annotation;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.model.entity.Entity;

import java.lang.annotation.*;

/**
 * 继承约束
 *
 * @author jianglei
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface InheritConstraint {
    /**
     * 被继承约束的对应实体的指定属性名称，默认为当前属性名称
     *
     * @return 被继承约束的对应实体的指定属性名称
     */
    String value() default Strings.EMPTY;

    /**
     * 被继承的实体类型
     *
     * @return 被继承的实体类型
     */
    Class<? extends Entity> type() default Entity.class;
}
