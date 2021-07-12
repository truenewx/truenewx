package org.truenewx.tnxjee.core.spec;

/**
 * 长整型值的可选项
 */
public class LongSelectableItem extends AbstractSelectableItem<Long> {

    public LongSelectableItem(Long value, String text, String index) {
        super(value, text, index);
    }

    public LongSelectableItem(Long value, String text) {
        super(value, text);
    }

    public LongSelectableItem() {
    }

}
