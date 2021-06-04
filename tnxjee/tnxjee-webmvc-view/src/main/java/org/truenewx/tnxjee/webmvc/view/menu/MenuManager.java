package org.truenewx.tnxjee.webmvc.view.menu;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.webmvc.view.menu.model.Menu;

/**
 * 菜单工厂
 *
 * @author jianglei
 */
public interface MenuManager {

    /**
     * 获取包含全部内容的完整菜单
     *
     * @return 完整菜单
     */
    Menu getFullMenu();

    /**
     * 获取内容与指定授权匹配的菜单，是完整菜单的子集
     *
     * @param authorities 所获权限集
     * @return 获权菜单
     */
    Menu getGrantedMenu(Collection<? extends GrantedAuthority> authorities);

}
