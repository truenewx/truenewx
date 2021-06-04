package org.truenewx.tnxjee.model.entity.unity;

import java.io.Serializable;

/**
 * 从属单体
 *
 * @author jianglei
 * @param <K> 标识类型
 * @param <O> 所属者类型
 */
public interface OwnedUnity<K extends Serializable, O extends Serializable> extends Unity<K> {
    /**
     * @return 所有者
     */
    O getOwner();
}
