package org.truenewx.tnxjee.core.spec;

/**
 * 长整型值的可选项
 */
public class StringSelectableItem extends AbstractSelectableItem<String> {

    public StringSelectableItem(String value, String text, String index) {
        super(value, text, index);
    }

    public StringSelectableItem(String value, String text) {
        super(value, text);
    }

    public StringSelectableItem() {
    }

}
