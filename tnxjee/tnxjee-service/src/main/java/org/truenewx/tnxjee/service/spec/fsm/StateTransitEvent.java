package org.truenewx.tnxjee.service.spec.fsm;

import java.io.Serializable;

import org.springframework.context.ApplicationEvent;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 状态转换事件
 *
 * @param <K> 标识类型
 * @param <T> 转换枚举类型
 * @author jianglei
 */
public class StateTransitEvent<I extends UserIdentity<?>, K extends Serializable, T extends Enum<T>>
        extends ApplicationEvent {

    private static final long serialVersionUID = 5419560334614517108L;

    private I userIdentity;
    private K key;
    private T transition;
    private Object context;

    public StateTransitEvent(Object source, I userIdentity, K key, T transition, Object context) {
        super(source);
        this.userIdentity = userIdentity;
        this.key = key;
        this.transition = transition;
        this.context = context;
    }

    public StateTransitEvent(Object source, I userIdentity, K key, T transition) {
        this(source, userIdentity, key, transition, null);
    }

    public I getUserIdentity() {
        return this.userIdentity;
    }

    public K getKey() {
        return this.key;
    }

    public T getTransition() {
        return this.transition;
    }

    @SuppressWarnings("unchecked")
    public <C> C getContext() {
        return (C) this.context;
    }

}
