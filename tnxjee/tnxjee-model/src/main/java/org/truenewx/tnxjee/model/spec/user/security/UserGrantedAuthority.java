package org.truenewx.tnxjee.model.spec.user.security;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 用户已获权限
 */
public class UserGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = -2834509708888634914L;

    private String type;
    private String rank;
    private String app;
    private Set<String> permissions;

    public UserGrantedAuthority() {
    }

    public UserGrantedAuthority(String type, String rank, String app) {
        setType(type);
        setRank(rank);
        setApp(app);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        if (StringUtils.isNotBlank(type)) {
            this.type = type;
        } else {
            this.type = null;
        }
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        if (StringUtils.isNotBlank(rank)) {
            this.rank = rank;
        } else {
            this.rank = null;
        }
    }

    public String getApp() {
        return this.app;
    }

    public void setApp(String app) {
        if (StringUtils.isNotBlank(app)) {
            this.app = app;
        } else {
            this.app = null;
        }
    }

    public Set<String> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    //////

    public void addPermission(String permission) {
        if (StringUtils.isNotBlank(permission)) {
            if (this.permissions == null) {
                this.permissions = new LinkedHashSet<>();
            }
            this.permissions.add(permission);
        }
    }

    public void addPermissions(Collection<String> permissions) {
        if (CollectionUtils.isNotEmpty(permissions)) {
            if (this.permissions == null) {
                this.permissions = new LinkedHashSet<>();
            }
            this.permissions.addAll(permissions);
        }
    }

    public void addPermissions(String[] permissions) {
        if (ArrayUtils.isNotEmpty(permissions)) {
            if (this.permissions == null) {
                this.permissions = new LinkedHashSet<>();
            }
            for (String permission : permissions) {
                this.permissions.add(permission);
            }
        }
    }

    public boolean matches(String type, String rank, String app, String permission) {
        if (StringUtils.isNotBlank(type) && !Strings.ASTERISK.equals(this.type) && !type.equals(this.type)) {
            return false;
        }
        if (StringUtils.isNotBlank(rank) && !Strings.ASTERISK.equals(this.rank) && !rank.equals(this.rank)) {
            return false;
        }
        if (StringUtils.isNotBlank(permission)) {
            // 限定了许可才判断应用限定
            if (StringUtils.isNotBlank(app) && !Strings.ASTERISK.equals(this.app) && !app.equals(this.app)) {
                return false;
            }
            if (this.permissions != null) {
                for (String pattern : this.permissions) {
                    // 有一个许可忽略大小写通配符匹配，则视为匹配
                    if (StringUtil.wildcardMatch(permission.toLowerCase(), pattern.toLowerCase())) {
                        return true;
                    }
                }
            }
            // 限定了许可但未包含，则不匹配
            return false;
        }
        return true;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        // 形如： type|rank|permission0,permission1,...
        StringBuilder authority = new StringBuilder();
        StringUtil.ifNotBlank(this.type, (Consumer<String>) authority::append);
        authority.append(Strings.VERTICAL_BAR);
        StringUtil.ifNotBlank(this.rank, (Consumer<String>) authority::append);
        authority.append(Strings.VERTICAL_BAR);
        StringUtil.ifNotBlank(this.app, (Consumer<String>) authority::append);
        authority.append(Strings.VERTICAL_BAR);
        if (this.permissions != null) {
            authority.append(StringUtils.join(this.permissions, Strings.COMMA));
        }
        return authority.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserGrantedAuthority that = (UserGrantedAuthority) o;
        return getAuthority().equals(that.getAuthority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.rank, this.app, this.permissions);
    }

    @Override
    public String toString() {
        return getAuthority();
    }

}
