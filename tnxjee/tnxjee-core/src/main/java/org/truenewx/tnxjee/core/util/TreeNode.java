package org.truenewx.tnxjee.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 树节点
 *
 * @author jianglei
 */
public class TreeNode<K extends Serializable> implements Serializable {

    private static final long serialVersionUID = 7180793290171989639L;

    private K id;
    private String caption;
    private TreeNode<K> parent;
    private List<TreeNode<K>> subs = new ArrayList<>();

    public TreeNode(K id, String caption) {
        this.id = id;
        this.caption = caption;
    }

    public K getId() {
        return this.id;
    }

    public String getCaption() {
        return this.caption;
    }

    public TreeNode<K> getParent() {
        return this.parent;
    }

    public List<TreeNode<K>> getSubs() {
        // 不直接返回subs引用，以免直接添加子节点，导致不能自动设置子节点的父引用
        return this.subs.isEmpty() ? null : Collections.unmodifiableList(this.subs);
    }

    public boolean isLeaf() {
        return this.subs.isEmpty();
    }

    public K getParentId() {
        return this.parent == null ? null : this.parent.getId();
    }

    public void addSub(TreeNode<K> sub) {
        this.subs.add(sub);
        sub.parent = this;
    }

    public TreeNode<K> getSub(K subId) {
        for (TreeNode<K> sub : this.subs) {
            if (sub.getId().equals(subId)) {
                return sub;
            }
        }
        return null;
    }

    public void sortSubs(Comparator<TreeNode<K>> comparator) {
        this.subs.sort(comparator);
    }

}
