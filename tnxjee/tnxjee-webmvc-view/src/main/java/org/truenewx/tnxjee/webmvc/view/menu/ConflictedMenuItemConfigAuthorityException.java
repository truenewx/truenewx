package org.truenewx.tnxjee.webmvc.view.menu;

import org.truenewx.tnxjee.webmvc.view.menu.model.MenuItem;

/**
 * 冲突的菜单项配置权限异常
 */
public class ConflictedMenuItemConfigAuthorityException extends RuntimeException {

    private static final long serialVersionUID = -5939771245945062923L;

    public ConflictedMenuItemConfigAuthorityException(MenuItem item) {
        super("Conflicted config authority: " + item.getPath());
    }

}
