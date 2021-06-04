package org.truenewx.tnxjee.core.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 具有数量限制的Map，继承自{@link LinkedHashMap}。<br>
 * 当Map大小达到最大值后，优先移除最早被加入的条目，put()和putAll()方法会更新条目的顺序
 * 
 * @author jianglei
 * 
 * @param <K>
 *            键类型
 * @param <V>
 *            值类型
 */
public class MaxSizeMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 2281427155741137971L;

    private int maxSize;
    private boolean updateOrderOnGet;

    public MaxSizeMap(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be >0");
        }
        this.maxSize = maxSize;
    }

    /**
     * 
     * @param maxSize
     *            允许的最大数量
     * @param updateOrderOnGet
     *            是否在调用get()方法时更新条目的顺序
     */
    public MaxSizeMap(int maxSize, boolean updateOrderOnGet) {
        this(maxSize);
        this.updateOrderOnGet = updateOrderOnGet;
    }

    /**
     * 更新指定关键字对应条目的顺序
     * 
     * @param key
     *            关键字
     * @return 是否更新成功，如果指定关键字对应的条目不存在则不成功
     */
    public boolean update(K key) {
        final V value = remove(key);
        if (value != null) {
            super.put(key, value);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        if (this.updateOrderOnGet) {
            final V value = remove(key);
            if (value != null) {
                super.put((K) key, value);
            }
            return value;
        } else {
            return super.get(key);
        }
    }

    @Override
    public V put(K key, V value) {
        remove(key); // 先删除已有的，不管原来是否存在，以更新key的顺序
        final V result = super.put(key, value);
        adjustSize();
        return result;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        for (final K key : t.keySet()) {
            remove(key); // 先删除已有的，不管原来是否存在，以更新key的顺序
        }
        super.putAll(t);
        adjustSize();
    }

    private void adjustSize() {
        final int size = size();
        if (size > this.maxSize) {
            final int count = size - this.maxSize;
            final List<K> removedKeys = new ArrayList<K>();
            int i = 0;
            for (final K key : keySet()) {
                if (i++ >= count) {
                    break;
                }
                removedKeys.add(key);
            }
            for (final K key : removedKeys) {
                remove(key);
            }
        }
    }
}
