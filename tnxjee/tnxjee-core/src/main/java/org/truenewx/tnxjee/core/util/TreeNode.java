package org.truenewx.tnxjee.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 树节点
 *
 * @author jianglei
 * 
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
        return Collections.unmodifiableList(this.subs);
    }

    public K getParentId() {
        return this.parent == null ? null : this.parent.getId();
    }

    public void addSub(TreeNode<K> sub) {
        this.subs.add(sub);
        sub.parent = this;
    }

}
