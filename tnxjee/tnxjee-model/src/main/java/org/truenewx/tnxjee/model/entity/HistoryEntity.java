package org.truenewx.tnxjee.model.entity;

/**
 * 历史实体
 *
 * @param <P> 对应的当前实体类型
 */
// 转换方法不提供默认实现，默认实现必须通过反射机制，性能较差，且容易出现不符合业务实际情况的错误拷贝，尤其是引用属性
public interface HistoryEntity<P extends Entity> extends Entity {

    P toPresent();

    void fromPresent(P present);

}
