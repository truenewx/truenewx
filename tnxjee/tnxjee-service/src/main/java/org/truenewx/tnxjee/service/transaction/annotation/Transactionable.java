package org.truenewx.tnxjee.service.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可具有事务的
 *
 * @author jianglei
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactionable {

    /**
     *
     * @return 代理接口类型
     */
    Class<?>[] proxyInterface();

    /**
     *
     * @return 将具有只读事务的方法名称式样
     */
    String[] read() default {};

    /**
     *
     * @return 将具有可写事务的方法名称式样
     */
    String[] write() default {};
}
