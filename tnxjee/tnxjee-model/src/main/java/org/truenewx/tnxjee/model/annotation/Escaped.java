package org.truenewx.tnxjee.model.annotation;

import java.lang.annotation.*;

/**
 * 字段/参数/结果转义标注，被标注的字段/参数/结果会被进行转义处理，以确保XSS安全
 */
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Escaped {

}
