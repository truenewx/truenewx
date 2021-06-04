package org.truenewx.tnxjee.core.enums;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.truenewx.tnxjee.core.spec.BooleanEnum;
import org.truenewx.tnxjee.core.spec.Named;

/**
 * 枚举类型
 *
 * @author jianglei
 */
public class EnumType implements Named {
    /**
     * 名称
     */
    private String name;
    /**
     * 子名称
     */
    private String subname;
    /**
     * 说明
     */
    private String caption;
    /**
     * 枚举项映射集
     */
    private Map<String, EnumItem> items = new LinkedHashMap<>();

    public EnumType(String name, String caption) {
        this.name = name;
        this.caption = caption;
    }

    public EnumType(String name, String subname, String caption) {
        this.name = name;
        this.subname = subname;
        this.caption = caption;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getSubname() {
        return this.subname;
    }

    public String getCaption() {
        return this.caption;
    }

    public void addItem(EnumItem item) {
        this.items.put(item.getKey(), item);
    }

    public EnumItem getItem(String key, String... keys) {
        if (BooleanEnum.class.getName().equals(this.name)) {
            key = key.toUpperCase();
        }
        EnumItem item = this.items.get(key);
        if (item != null && keys.length > 0) {
            String[] subkeys = new String[keys.length - 1];
            if (subkeys.length > 0) {
                System.arraycopy(keys, 1, subkeys, 0, subkeys.length);
                item = item.getChild(keys[0], subkeys);
            }
        }
        return item;
    }

    /**
     * 获取所有直接枚举项
     *
     * @return 所有直接枚举项
     */
    public Collection<EnumItem> getItems() {
        return this.items.values().stream().sorted().collect(Collectors.toList());
    }

    /**
     * 设置直接枚举项集
     *
     * @param items 直接枚举项集
     */
    public void setItems(Collection<EnumItem> items) {
        synchronized (this.items) {
            this.items.clear();
            for (EnumItem item : items) {
                addItem(item);
            }
        }
    }

    public EnumItem getItemByCaption(String caption) {
        for (EnumItem item : this.items.values()) {
            if (item.getCaption().equals(caption)) {
                return item;
            }
        }
        return null;
    }
}
