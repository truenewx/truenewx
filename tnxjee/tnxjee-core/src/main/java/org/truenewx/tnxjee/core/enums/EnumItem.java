package org.truenewx.tnxjee.core.enums;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * 枚举项
 *
 * @author jianglei
 */
public class EnumItem implements Comparable<EnumItem> {

    private int ordinal;
    private String key;
    private String caption;
    private String searchIndex;
    private Map<String, EnumItem> children = new LinkedHashMap<>();

    public EnumItem(int ordinal, String key, String caption) {
        if (key == null) {
            throw new IllegalArgumentException("The key must be not null");
        }
        this.ordinal = ordinal;
        this.key = key;
        this.caption = caption;
        if (caption != null) { // 默认以显示名称拼音作为搜索索引
            this.searchIndex = StringUtil.toPinyin(caption);
        }
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public String getKey() {
        return this.key;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getSearchIndex() {
        return this.searchIndex;
    }

    public void setSearchIndex(String searchIndex) {
        this.searchIndex = searchIndex;
    }

    public boolean matches(String keyword) {
        return StringUtils.isBlank(keyword) || StringUtil.matchesForEach(this.key, keyword)
                || StringUtil.matchesForEach(this.caption, keyword)
                || StringUtil.matchesForEach(this.searchIndex, keyword);
    }

    /**
     * 添加一个子项
     *
     * @param child 子项
     */
    public void addChild(EnumItem child) {
        this.children.put(child.getKey(), child);
    }

    /**
     * 添加多个子项
     *
     * @param children 子项集
     */
    public void addChildren(Collection<EnumItem> children) {
        synchronized (this.children) {
            for (EnumItem child : children) {
                addChild(child);
            }
        }
    }

    /**
     * 设置子项集
     *
     * @param children 子项集
     */
    public void setChildren(Collection<EnumItem> children) {
        this.children.clear();
        addChildren(children);
    }

    public Iterable<EnumItem> getChildren() {
        return this.children.isEmpty() ? null : this.children.values();
    }

    /**
     * 逐级获取指定键的子级枚举项
     *
     * @param key  键
     * @param keys 其它键
     * @return 子级枚举项
     */
    public EnumItem getChild(String key, String... keys) {
        EnumItem child = this.children.get(key);
        for (String k : keys) {
            if (child == null) {
                break;
            }
            child = child.getChild(k);
        }
        return child;
    }

    public Set<String> getChildNames() {
        return this.children.keySet();
    }

    public List<EnumItem> getChildrenPath(String... keys) {
        List<EnumItem> children = new ArrayList<>();
        if (keys.length == 0) {
            children.addAll(this.children.values());
            Collections.sort(children);
        } else {
            EnumItem child = getChild(null, keys);
            if (child != null) {
                return child.getChildrenPath();
            }
        }
        return children;
    }

    public EnumItem getChildByCaption(String caption) {
        if (caption != null) {
            for (EnumItem item : this.children.values()) {
                if (caption.equals(item.getCaption())) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 获取指定显示说明对应的枚举项相对顶级枚举类型项的枚举项路径
     *
     * @param caption 显示说明
     * @return 枚举项路径
     */
    public List<EnumItem> getChildrenPathByCaption(String caption) {
        List<EnumItem> chain = new ArrayList<>();
        for (EnumItem child : this.children.values()) {
            if (child.getCaption().equals(caption)) {
                chain.add(child);
                break;
            }
            List<EnumItem> childChain = child.getChildrenPathByCaption(caption);
            if (childChain != null && childChain.size() > 0) {
                chain.add(child);
                chain.addAll(childChain);
                break;
            }
        }
        return chain;
    }

    @Override
    public String toString() {
        return "[" + this.ordinal + "]" + this.key + "=" + this.caption;
    }

    @Override
    public int compareTo(EnumItem other) {
        if (this.ordinal == other.ordinal) { // 序号相等时比较其它两个属性
            if (this.caption == null || this.caption.equals(other.caption)) { // 当前说明为空或两个说明相等时，最后用键排序
                return this.key.compareTo(other.key);
            } else { // 其次用说明排序
                return this.caption.compareTo(other.caption);
            }
        } else { // 优先用序号排序
            return Integer.valueOf(this.ordinal).compareTo(other.ordinal);
        }
    }

}
