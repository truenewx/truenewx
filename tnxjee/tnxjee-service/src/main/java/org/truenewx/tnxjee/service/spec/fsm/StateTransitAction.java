package org.truenewx.tnxjee.service.spec.fsm;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 状态转换动作
 *
 * @param <U> 单体类型
 * @param <K> 标识类型
 * @param <S> 状态枚举类型
 * @param <T> 转换枚举类型
 * @param <I> 用户标识类型
 * @author jianglei
 */
public interface StateTransitAction<U extends Unity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity<?>> {
    /**
     * 获取转换枚举。每个转换动作都对应且仅对应一个转换枚举
     *
     * @return 转换枚举
     */
    T getTransition();

    /**
     * @return 当前转换动作可能的开始状态集
     */
    S[] getBeginStates();

    /**
     * 获取在指定开始状态执行当前转换动作后的结束状态
     *
     * @param beginState 开始状态
     * @param condition  条件
     * @return 结束状态，如果在指定起始状态下不能根据指定条件执行当前转换动作，则返回null
     */
    S getEndState(S beginState, Object condition);

    /**
     * 检查指定用户对指定单体是否具有当前动作的操作权限
     *
     * @param userIdentity 用户标识
     * @param unity        单体
     * @return 是否具有数据操作权限
     */
    boolean isGranted(I userIdentity, U unity);

    /**
     * 指定用户对指定单体，在指定上下文情况时，执行动作
     *
     * @param userIdentity 用户标识
     * @param unity        单体
     * @param endState     执行动作后应该处于的结束状态
     * @param context      上下文
     * @return 动作是否已正常执行
     */
    boolean execute(I userIdentity, U unity, S endState, Object context);

}
