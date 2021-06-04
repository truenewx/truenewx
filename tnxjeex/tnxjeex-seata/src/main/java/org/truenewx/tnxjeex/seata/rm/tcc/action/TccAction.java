package org.truenewx.tnxjeex.seata.rm.tcc.action;

import io.seata.rm.tcc.api.BusinessActionContext;

/**
 * TCC操作
 */
public interface TccAction {

    boolean commit(BusinessActionContext actionContext);

    boolean rollback(BusinessActionContext actionContext);

}
