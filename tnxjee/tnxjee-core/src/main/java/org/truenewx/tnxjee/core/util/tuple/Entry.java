package org.truenewx.tnxjee.core.util.tuple;

/**
 * 键值对条目
 *
 * @author jianglei
 * 
 * @param <K> 键类型
 * @param <V> 值类型
 */
public interface Entry<K, V> {

    /**
     * 获取键
     *
     * @return 键
     */
    K getKey();

    /**
     * 获取值
     *
     * @return 值
     */
    V getValue();

}