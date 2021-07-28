package org.truenewx.tnxjee.webmvc.view.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.service.security.access.GrantedAuthorityDecider;
import org.truenewx.tnxjee.webmvc.security.web.access.ConfigAuthorityResolver;
import org.truenewx.tnxjee.webmvc.view.menu.config.MenuProperties;
import org.truenewx.tnxjee.webmvc.view.menu.model.Menu;
import org.truenewx.tnxjee.webmvc.view.menu.model.MenuItem;
import org.truenewx.tnxjee.webmvc.view.menu.parser.MenuParser;

/**
 * 菜单工厂实现
 *
 * @author jianglei
 */
@Component
public class MenuManagerImpl implements MenuManager, InitializingBean {

    private String profile;
    private MenuParser parser;
    private Menu menu;
    @Autowired
    private MenuProperties properties;
    @Autowired
    private ConfigAuthorityResolver authorityResolver;
    @Autowired
    private GrantedAuthorityDecider authorityDecider;
    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String appName;

    @Autowired
    public void setParser(MenuParser parser) {
        this.parser = parser;
    }

    @Autowired
    public void setProfileSupplier(ProfileSupplier profileSupplier) {
        this.profile = profileSupplier.get();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource location = this.properties.getLocation();
        if (location == null) {
            location = this.parser.getDefaultLocation();
        }
        Menu menu = this.parser.parse(location);
        if (menu != null && menu.matchesProfile(this.profile)) {
            this.menu = menu;
        }
    }

    @Override
    public Menu getFullMenu() {
        return this.menu;
    }

    @Override
    public Menu getGrantedMenu(Collection<? extends GrantedAuthority> authorities) {
        if (this.menu == null) {
            return null;
        }
        Menu menu = this.menu.clone(false);
        List<MenuItem> items = menu.getItems();
        this.menu.getItems().forEach(item -> {
            cloneGrantedItemTo(authorities, item, items);
        });
        return menu;
    }

    private void cloneGrantedItemTo(Collection<? extends GrantedAuthority> grantedAuthorities, MenuItem item,
            List<MenuItem> items) {
        if (item.matchesProfile(this.profile)) { // 首先需匹配运行环境
            MenuItem grantedItem = null;

            // 权限匹配，菜单项不带子菜单项加入目标集合
            if (isGranted(grantedAuthorities, getConfigAuthorities(item))) {
                grantedItem = item.clone(false);
                items.add(grantedItem);
            }
            // 获取子菜单项中权限匹配的菜单项集合
            List<MenuItem> grantedSubs = new ArrayList<>();
            for (MenuItem sub : item.getSubs()) {
                cloneGrantedItemTo(grantedAuthorities, sub, grantedSubs);
            }
            if (grantedSubs.size() > 0) {
                if (grantedItem == null) { // 下级菜单项包含有匹配的，则该菜单项视为匹配
                    grantedItem = item.clone(false);
                    items.add(grantedItem);
                }
                grantedItem.setSubs(grantedSubs);
            }
        }
    }

    private Collection<UserConfigAuthority> getConfigAuthorities(MenuItem item) {
        String rank = item.getRank();
        String permission = item.getPermission();
        // 用户类型是一定在菜单中有配置的，所以不视为在菜单中配置权限的标志
        boolean menuConfigured = StringUtils.isNotBlank(rank) || StringUtils.isNotBlank(permission);
        Collection<UserConfigAuthority> configAuthorities = this.authorityResolver
                .resolveConfigAuthorities(item.getPath(), HttpMethod.GET);
        if (configAuthorities != null) {
            for (UserConfigAuthority configAuthority : configAuthorities) {
                // 不允许菜单配置中有权限配置，同时对应的Controller方法上也有权限配置，且两者不一致
                if (menuConfigured && (!StringUtil.equalsIgnoreBlank(this.menu.getUserType(), configAuthority.getType())
                        || !StringUtil.equalsIgnoreBlank(rank, configAuthority.getRank())
                        || !StringUtil.equalsIgnoreBlank(this.appName, configAuthority.getApp())
                        || !StringUtil.equalsIgnoreBlank(permission, configAuthority.getPermission()))) {
                    throw new ConflictedMenuItemConfigAuthorityException(item);
                }
            }
            return configAuthorities;
        }
        if (menuConfigured) {
            return Collections.singletonList(
                    new UserConfigAuthority(this.menu.getUserType(), rank, this.appName, permission, false));
        }
        // 两者都没有权限配置时返回null
        return null;
    }

    private boolean isGranted(Collection<? extends GrantedAuthority> grantedAuthorities,
            Collection<UserConfigAuthority> configAuthorities) {
        if (configAuthorities == null) {
            // 即使允许匿名访问也必须配置匿名限定，没有权限限定是不允许出现的情况，视为获权失败
            return false;
        }
        for (UserConfigAuthority configAuthority : configAuthorities) {
            // 有一个权限匹配，就视为具有权限
            if (this.authorityDecider
                    .isGranted(grantedAuthorities, configAuthority.getType(), configAuthority.getRank(),
                            configAuthority.getApp(), configAuthority.getPermission())) {
                return true;
            }
        }
        return false;
    }

}
