package org.truenewx.tnxjeex.seata.rm.tcc.action;

import org.truenewx.tnxjeex.seata.rm.tcc.TccContext;

import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;

/**
 * TCC操作支持<br/>
 * 并不是TCC操作实现必须继承的父类，但强烈建议继承使用
 */
public abstract class TccActionSupport implements TccAction {

    private TccContext context = new TccContext();

    protected final String getXid(BusinessActionContext actionContext) {
        return actionContext == null ? RootContext.getXID() : actionContext.getXid();
    }

    protected final void saveContextValue(BusinessActionContext actionContext, Object value) {
        this.context.put(getXid(actionContext), value);
    }

    protected final <T> T removeContextValue(BusinessActionContext actionContext) {
        return this.context.remove(getXid(actionContext));
    }

}
