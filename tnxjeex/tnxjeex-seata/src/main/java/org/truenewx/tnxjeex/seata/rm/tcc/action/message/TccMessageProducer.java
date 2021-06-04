package org.truenewx.tnxjeex.seata.rm.tcc.action.message;

import org.truenewx.tnxjeex.seata.rm.tcc.action.TccAction;

import io.seata.rm.tcc.api.BusinessActionContext;

/**
 * TCC消息生产者
 */
public interface TccMessageProducer<T> extends TccAction {

    void prepare(BusinessActionContext actionContext, T payload);

}
