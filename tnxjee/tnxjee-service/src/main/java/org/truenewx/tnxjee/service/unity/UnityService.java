package org.truenewx.tnxjee.service.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.service.Service;

/**
 * 基于单体的服务
 *
 * @param <T> 单体类型
 * @param <K> 单体标识类型
 * @author jianglei
 */
public interface UnityService<T extends Unity<K>, K extends Serializable> extends Service {
    /**
     * 查找指定标识的单体，如果找不到则返回null
     *
     * @param id 单体标识
     * @return 单体
     */
    T find(K id);

    /**
     * 加载指定标识的单体，如果找不到则抛出异常
     *
     * @param id 单体标识
     * @return 单体
     */
    T load(K id);

    /**
     * 删除单体
     *
     * @param id 要删除的单体的标识
     * @return 被删除的单体，如果实际上未删除任何实体，则返回null
     */
    T delete(K id);
}
