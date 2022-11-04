package org.truenewx.tnxjee.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.tnxjee.core.Strings;

/**
 * RPC接口标注
 *
 * @author jianglei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcApi {

    /**
     * @return 业务类型，不能包含斜杠/
     */
    String value() default Strings.EMPTY;

}
