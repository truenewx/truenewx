package org.truenewx.tnxjeex.seata.rm.tcc.action.message;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.truenewx.tnxjeex.seata.rm.tcc.action.TccActionSupport;

import io.seata.rm.tcc.api.BusinessActionContext;

/**
 * TCC消息生产者支持
 */
public abstract class TccMessageProducerSupport<T> extends TccActionSupport implements TccMessageProducer<T> {

    @Override
    public void prepare(BusinessActionContext actionContext, T payload) {
        saveContextValue(actionContext, MessageBuilder.withPayload(payload).build());
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        Message<T> message = removeContextValue(actionContext);
        if (message != null) {
            getOutputChannel().send(message);
        }
        return true;
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        removeContextValue(actionContext);
        return true;
    }

    protected abstract MessageChannel getOutputChannel();

}
