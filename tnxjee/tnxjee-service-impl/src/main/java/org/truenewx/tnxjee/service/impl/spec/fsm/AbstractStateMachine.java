package org.truenewx.tnxjee.service.impl.spec.fsm;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.exception.NoDataOperateAuthorityException;
import org.truenewx.tnxjee.service.impl.ServiceSupport;
import org.truenewx.tnxjee.service.spec.fsm.StateIntransitableException;
import org.truenewx.tnxjee.service.spec.fsm.StateMachine;
import org.truenewx.tnxjee.service.spec.fsm.StateTransitAction;
import org.truenewx.tnxjee.service.transaction.annotation.WriteTransactional;

/**
 * 抽象的有限状态机
 *
 * @param <U> 单体类型
 * @param <K> 标识类型
 * @param <S> 状态枚举类型
 * @param <T> 转换枚举类型
 * @param <I> 用户标识类型
 * @author jianglei
 */
public abstract class AbstractStateMachine<U extends Unity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity<?>>
        extends ServiceSupport implements StateMachine<U, K, S, T, I> {
    /**
     * 起始状态
     */
    private S startState;
    /**
     * 转换动作映射集：转换-动作
     */
    private Map<T, StateTransitAction<U, K, S, T, I>> transitionActionMapping = new HashMap<>();

    public AbstractStateMachine(S startState) {
        this.startState = startState;
    }

    public void setActions(Collection<? extends StateTransitAction<U, K, S, T, I>> actions) {
        this.transitionActionMapping.clear();
        actions.forEach(action -> {
            this.transitionActionMapping.put(action.getTransition(), action);
        });
    }

    @Override
    public S getStartState() {
        return this.startState;
    }

    @Override
    public Set<T> getTransitions(S state) {
        Set<T> transitions = new HashSet<>();
        this.transitionActionMapping.values().forEach(action -> {
            if (ArrayUtils.contains(action.getBeginStates(), state)) {
                transitions.add(action.getTransition());
            }
        });
        return transitions;
    }

    @Override
    public S[] getBeginStates(T transition) {
        StateTransitAction<U, K, S, T, I> action = this.transitionActionMapping.get(transition);
        return action == null ? null : action.getBeginStates();
    }

    @Override
    public S getEndState(S beginState, T transition, Object condition) {
        StateTransitAction<U, K, S, T, I> action = this.transitionActionMapping.get(transition);
        return action == null ? null : action.getEndState(beginState, condition);
    }

    @Override
    @WriteTransactional
    public U transit(I userIdentity, K id, T transition, Object context) {
        U unity = loadUnity(id);
        S state = getState(unity);
        StateTransitAction<U, K, S, T, I> action = this.transitionActionMapping.get(transition);
        if (action == null) {
            throw new StateIntransitableException(state, transition);
        }
        if (!action.isGranted(userIdentity, unity)) {
            throw new NoDataOperateAuthorityException();
        }
        Object condition = getCondition(userIdentity, unity, context);
        S endState = action.getEndState(state, condition);
        if (endState == null) {
            throw new StateIntransitableException(state, transition);
        }
        if (!action.execute(userIdentity, unity, endState, context)) {
            return null;
        }
        // 正常执行完毕后，最新的状态应该等于指定的结束状态
        Assert.isTrue(getState(unity) == endState, () -> {
            return "The last state of " + unity + " should be " + endState;
        });
        return unity;
    }

    /**
     * 加载指定单体，需确保返回非空的单体，如果找不到指定单体，则需抛出业务异常
     *
     * @param id 单体id
     * @return 单体
     */
    protected abstract U loadUnity(K id);

    /**
     * 从指定单体中获取状态值。单体可能包含多个状态属性，故不通过让单体实现获取状态的接口来实现
     *
     * @param unity 单体
     * @return 状态值
     */
    protected abstract S getState(U unity);

    /**
     * 获取转换条件，用于定位转换动作
     *
     * @param userIdentity 用户标识
     * @param unity        单体
     * @param context      转换上下文
     * @return 转换条件
     */
    protected abstract Object getCondition(I userIdentity, U unity, Object context);

}
