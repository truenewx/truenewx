package org.truenewx.tnxjee.webmvc.view.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.core.util.CollectionUtil;

/**
 * 菜单项
 */
public class MenuItem extends MenuElement {

    private static final long serialVersionUID = 1338297288402064073L;

    private String path;
    private String rank;
    private String permission;
    private String type;
    private String target;
    private String icon;

    private List<MenuItem> subs = new ArrayList<>();

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPermission() {
        return this.permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<MenuItem> getSubs() {
        return this.subs;
    }

    public void setSubs(List<MenuItem> subs) {
        CollectionUtil.reset(subs, this.subs);
    }

    //////

    public MenuItem clone(boolean withSubs) {
        MenuItem target = super.cloneTo(new MenuItem());
        target.path = this.path;
        target.rank = this.rank;
        target.permission = this.permission;
        target.type = this.type;
        target.target = this.target;
        target.icon = this.icon;
        if (withSubs) {
            target.subs.clear();
            this.subs.forEach(item -> {
                target.subs.add(item.clone(true));
            });
        }
        return target;
    }

}
