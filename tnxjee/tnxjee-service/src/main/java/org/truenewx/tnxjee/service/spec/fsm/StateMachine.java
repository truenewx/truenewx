package org.truenewx.tnxjee.service.spec.fsm;

import java.io.Serializable;
import java.util.Set;

import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.Service;


/**
 * 有限状态机
 *
 * @param <U> 单体类型
 * @param <K> 标识类型
 * @param <S> 状态枚举类型
 * @param <T> 转换枚举类型
 * @param <I> 用户标识类型
 * @author jianglei
 */
public interface StateMachine<U extends Unity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity<?>>
        extends Service {
    /**
     * 获取起始状态。有限状态机具有且仅具有一个起始状态
     *
     * @return 起始状态
     */
    S getStartState();

    /**
     * 获取在指定状态下可进行的转换清单
     *
     * @param state 状态
     * @return 可进行的转换清单
     */
    Set<T> getTransitions(S state);

    /**
     * 获取可进行指定转换的开始状态清单
     *
     * @param transition 转换
     * @return 可进行指定转换的开始状态清单
     */
    S[] getBeginStates(T transition);

    /**
     * 获取在指定开始状态下，在指定条件下，进行指定转换后将进入的结束状态
     *
     * @param beginState 开始状态
     * @param transition 转换
     * @param condition  条件
     * @return 结束状态
     */
    S getEndState(S beginState, T transition, Object condition);

    /**
     * 指定用户对指定单体进行指定转换
     *
     * @param userIdentity 用户标识
     * @param id           单体标识
     * @param transition   转换
     * @param context      上下文
     * @return 转换影响的单体
     */
    U transit(I userIdentity, K id, T transition, Object context);

}
