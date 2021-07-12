package org.truenewx.tnxjee.core.spec;

/**
 * 整型值的可选项
 */
public class IntegerSelectableItem extends AbstractSelectableItem<Integer> {

    public IntegerSelectableItem(Integer value, String text, String index) {
        super(value, text, index);
    }

    public IntegerSelectableItem(Integer value, String text) {
        super(value, text);
    }

    public IntegerSelectableItem() {
    }

}
