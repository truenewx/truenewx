package org.truenewx.tnxjee.core.spec;

import java.io.Serializable;

import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * 默认的可选项
 */
public abstract class AbstractSelectableItem<V extends Serializable> implements SelectableItem<V> {

    private V value;
    private String text;
    private String index;

    public AbstractSelectableItem(V value, String text, String index) {
        this.value = value;
        this.text = text;
        this.index = index;
    }

    public AbstractSelectableItem(V value, String text) {
        this(value, text, StringUtil.toPinyin(text));
    }

    public AbstractSelectableItem() {
    }

    @Override
    public V getValue() {
        return this.value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getIndex() {
        return this.index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

}
