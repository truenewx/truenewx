package org.truenewx.tnxjeex.seata.rm.tcc.action;

import io.seata.rm.tcc.api.BusinessActionContext;

/**
 * TCC操作控制器支持<br/>
 * 并不是TCC操作控制器必须继承的父类，但建议继承使用
 */
public abstract class TccActionControllerSupport { // 不能实现任何接口，否则子类控制器将无法被识别

    public abstract boolean commit(BusinessActionContext actionContext);

    public abstract boolean rollback(BusinessActionContext actionContext);

}
