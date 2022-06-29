package org.truenewx.tnxjee.model.entity.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.core.spec.Owned;

/**
 * 从属单体
 *
 * @param <K> 标识类型
 * @param <O> 所属者类型
 * @author jianglei
 */
public interface OwnedUnity<K extends Serializable, O extends Serializable> extends Unity<K>, Owned<O> {
}
