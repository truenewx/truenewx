package org.truenewx.tnxjee.webmvc.view.tagext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.util.BeanUtil;

/**
 * 选项标签支持
 *
 * @author jianglei
 */
public abstract class ItemTagSupport extends UiTagSupport {
    protected Object items;
    protected Object value;
    protected boolean emptyItem;
    protected String emptyItemValue = Strings.EMPTY;
    protected String emptyItemText = "&nbsp;";
    protected String itemValueProperty;
    protected String itemTextProperty;
    protected String separator;

    public void setItems(Object items) {
        this.items = items;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setEmptyItem(String emptyItem) {
        this.emptyItem = Boolean.valueOf(emptyItem);
    }

    public void setEmptyItemValue(String emptyItemValue) {
        this.emptyItemValue = emptyItemValue;
    }

    public void setEmptyItemText(String emptyItemText) {
        this.emptyItemText = emptyItemText;
    }

    public void setItemValueProperty(String itemValueProperty) {
        this.itemValueProperty = itemValueProperty;
    }

    public void setItemTextProperty(String itemTextProperty) {
        this.itemTextProperty = itemTextProperty;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public void doTag() throws IOException {
        resolveItems(getItems());
    }

    protected final Iterable<?> getItems() {
        Iterable<?> items = null;
        if (this.items instanceof Map<?, ?>) {
            items = ((Map<?, ?>) this.items).entrySet();
        } else if (this.items instanceof Iterable<?>) {
            items = (Iterable<?>) this.items;
        } else if (this.items instanceof Object[]) {
            items = Arrays.asList((Object[]) this.items);
        }
        return items;
    }

    protected void resolveItems(Iterable<?> items) throws IOException {
        if (this.emptyItem) {
            resolveItem(this.emptyItemValue, this.emptyItemText);
            if (this.separator != null) { // 有分隔符，则在空选项后添加分隔符
                print(this.separator);
            }
        }
        if (items != null) {
            int i = 0;
            for (Object item : items) {
                if (this.separator != null && i++ > 0) { // 有分隔符且非首项，则在前面添加分隔符
                    print(this.separator);
                }
                resolveItem(item);
            }
        }
    }

    protected void resolveItem(Object item) throws IOException {
        resolveItem(getItemValue(item), getItemText(item));
    }

    protected String getItemValue(Object item) {
        Object value = null;
        if (item instanceof Entry) {
            value = ((Entry<?, ?>) item).getKey();
        } else if (StringUtils.isNotBlank(this.itemValueProperty)) {
            value = BeanUtil.getPropertyValue(item, this.itemValueProperty);
        } else {
            value = item;
        }
        return value == null ? null : value.toString();
    }

    protected String getItemText(Object item) {
        Object text = null;
        if (item instanceof Entry) {
            text = ((Entry<?, ?>) item).getValue();
        } else if (item instanceof Enum) {
            EnumDictResolver enumDictResolver = getBeanFromApplicationContext(EnumDictResolver.class);
            return enumDictResolver.getText((Enum<?>) item, getLocale());
        } else if (StringUtils.isNotBlank(this.itemTextProperty)) {
            text = BeanUtil.getPropertyValue(item, this.itemTextProperty);
        } else {
            text = item;
        }
        return text == null ? null : text.toString();
    }

    /**
     * 判断指定选项值是否当前值
     *
     * @param itemValue 选项值
     * @return 指定选项值是否当前值
     */
    protected boolean isCurrentValue(String itemValue) {
        String value = this.value == null ? Strings.EMPTY : this.value.toString();
        if (StringUtils.isEmpty(value)) { // 当前值为空，则看选项值是否为设置的空选项值
            return Objects.equals(this.emptyItemValue, itemValue);
        } else { // 当前值不为空，则看选项值是否为当前值
            return value.equals(itemValue);
        }
    }

    protected abstract void resolveItem(String value, String text) throws IOException;

}
