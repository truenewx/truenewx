package org.truenewx.tnxjee.service.spec.fsm;

import java.io.Serializable;

import org.springframework.context.event.EventListener;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 事件驱动的有限状态机
 *
 * @author jianglei
 */
public abstract class EventDrivenStateMachine<U extends Unity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity<?>, E extends StateTransitEvent<I, K, T>>
        extends AbstractStateMachine<U, K, S, T, I> {

    public EventDrivenStateMachine(S startState) {
        super(startState);
    }

    /**
     * 响应事件进行处理。<br>
     * 注意：本方法应该抽取至接口中，并标注@EventListener注解
     *
     * @param event 事件
     */
    @EventListener
    public void onEvent(E event) {
        transit(event.getUserIdentity(), event.getKey(), event.getTransition(), event.getContext());
    }

}
