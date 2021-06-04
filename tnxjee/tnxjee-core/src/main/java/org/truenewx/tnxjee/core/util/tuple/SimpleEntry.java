package org.truenewx.tnxjee.core.util.tuple;

import java.util.Objects;

/**
 * 简单键值对条目
 *
 * @author jianglei
 * 
 * @param <K>
 *            键类型
 * @param <V>
 *            值类型
 */
public class SimpleEntry<K, V> implements Binate<K, V>, Entry<K, V>, Cloneable {

    private K key;
    private V value;

    public SimpleEntry() {
    }

    public SimpleEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getLeft() {
        return getKey();
    }

    public void setLeft(K left) {
        setKey(left);
    }

    @Override
    public V getRight() {
        return getValue();
    }

    public void setRight(V right) {
        setValue(right);
    }

    @Override
    public K getKey() {
        return this.key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    public V setValue(V value) {
        V result = this.value;
        this.value = value;
        return result;
    }

    @Override
    public SimpleEntry<K, V> clone() {
        SimpleEntry<K, V> entry = new SimpleEntry<>();
        entry.key = this.key;
        entry.value = this.value;
        return entry;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SimpleEntry<?, ?> other = (SimpleEntry<?, ?>) obj;
        return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode() + 19 * this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value;
    }
}
