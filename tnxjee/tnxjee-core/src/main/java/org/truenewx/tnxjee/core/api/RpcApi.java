package org.truenewx.tnxjee.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC的API接口标注，用于在源代码中快速定位所有API接口
 *
 * @author jianglei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RpcApi {
}
