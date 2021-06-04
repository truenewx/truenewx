package org.truenewx.tnxjee.service.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.OwnedUnity;

/**
 * 简单的从属单体服务
 *
 * @author jianglei
 */
public interface SimpleOwnedUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends OwnedUnityService<T, K, O> {
    /**
     * 添加从属单体
     *
     * @param owner 所属者
     * @param unity 存放添加数据的单体对象
     * @return 添加的单体
     */
    T add(O owner, T unity);

    /**
     * 修改从属单体
     *
     * @param owner 所属者
     * @param id    要修改单体的标识
     * @param unity 存放修改数据的单体对象
     * @return 修改后的单体
     */
    T update(O owner, K id, T unity);
}
