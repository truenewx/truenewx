package org.truenewx.tnxjee.service.transaction.annotation;

import java.lang.annotation.*;

import org.springframework.transaction.annotation.Transactional;

/**
 * 只读事务
 *
 * @author jianglei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Transactional(readOnly = true)
public @interface ReadTransactional {
}
