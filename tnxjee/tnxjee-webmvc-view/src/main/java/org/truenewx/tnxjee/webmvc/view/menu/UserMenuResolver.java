package org.truenewx.tnxjee.webmvc.view.menu;

import java.util.List;

import org.springframework.lang.NonNull;
import org.truenewx.tnxjee.webmvc.view.menu.model.Menu;

/**
 * 用户菜单解决器
 */
public interface UserMenuResolver {

    /**
     * 获取当前用户获权的菜单
     *
     * @return 当前用户获权的菜单
     */
    Menu getUserGrantedMenu();

    /**
     * 获取指定地址在当前用户的获权菜单中的位置索引链
     *
     * @param path 地址
     * @return 指定地址在当前用户的获权菜单中的位置索引链
     */
    List<Integer> indexesOf(@NonNull String path);

}
