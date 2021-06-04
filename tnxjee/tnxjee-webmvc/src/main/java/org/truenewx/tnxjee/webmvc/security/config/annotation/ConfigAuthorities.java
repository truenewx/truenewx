package org.truenewx.tnxjee.webmvc.security.config.annotation;

import java.lang.annotation.*;

/**
 * 配置权限限定集
 *
 * @author jianglei
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigAuthorities {

    ConfigAuthority[] value() default {};

}
