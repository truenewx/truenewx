package org.truenewx.tnxjee.service.transaction.annotation;

import java.lang.annotation.*;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 可写事务
 *
 * @author jianglei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = { Throwable.class })
public @interface WriteTransactional {
}
