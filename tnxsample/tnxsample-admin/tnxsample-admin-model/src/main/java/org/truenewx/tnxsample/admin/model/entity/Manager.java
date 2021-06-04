package org.truenewx.tnxsample.admin.model.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.DefaultUserIdentity;
import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjee.model.spec.user.UserSpecific;
import org.truenewx.tnxjee.model.spec.user.security.DefaultUserSpecificDetails;
import org.truenewx.tnxjee.model.spec.user.security.UserGrantedAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsSpecialChars;
import org.truenewx.tnxsample.common.constant.AppNames;
import org.truenewx.tnxsample.common.constant.ManagerRanks;
import org.truenewx.tnxsample.common.constant.UserTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 管理员
 *
 * @author jianglei
 */
@Caption("管理员")
public class Manager implements Unity<Integer>, Comparable<Manager>, UserSpecific<IntegerUserIdentity> {

    private Integer id;

    @Caption("工号")
    private String jobNo;

    @Caption("用户名")
    @NotBlank
    @NotContainsSpecialChars
    private String username;

    @Caption("登录密码")
    @NotEmpty
    private String password;

    @Caption("头像")
    private String headImageUrl;

    @Caption("姓名")
    @NotBlank
    @NotContainsSpecialChars
    private String fullName;

    @Caption("索引名")
    private String indexName;

    @Caption("是否顶级管理员")
    private boolean top;

    @Caption("是否禁用")
    private boolean disabled;

    @Caption("创建时间")
    private Instant createTime;

    /**
     * 角色集
     */
    private Collection<Role> roles = new TreeSet<>();

    @Override
    public Integer getId() {
        return this.id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public String getJobNo() {
        return this.jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeadImageUrl() {
        return this.headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public boolean isTop() {
        return this.top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Collection<Role> getRoles() {
        return this.roles;
    }

    protected void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    //////

    public String getType() {
        return UserTypes.MANAGER;
    }

    public String getRank() {
        return isTop() ? ManagerRanks.TOP : ManagerRanks.NORMAL;
    }

    @Override
    public int compareTo(Manager other) {
        return getUsername().compareTo(other.getUsername());
    }

    @Override
    @JsonIgnore
    public DefaultUserIdentity getIdentity() {
        return new DefaultUserIdentity(getType(), getId());
    }

    @Override
    public String getCaption() {
        return getFullName();
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String app = AppNames.ADMIN;
        UserGrantedAuthority authority = new UserGrantedAuthority(getType(), getRank(), app);
        if (isTop()) { // 顶级管理员具有所有应用的所有权限
            authority.setApp(Strings.ASTERISK);
            authority.addPermission(Strings.ASTERISK);
        } else {
            String permissionPrefix = app + Strings.DOT;
            getRoles().forEach(role -> {
                role.getPermissions().forEach(permission -> {
                    if (permission.startsWith(permissionPrefix)) {
                        permission = permission.substring(permissionPrefix.length());
                    }
                    authority.addPermission(permission);
                });
            });
        }
        return Collections.singletonList(authority);
    }

    @JsonIgnore
    public UserSpecificDetails<? extends IntegerUserIdentity> getSpecificDetails() {
        DefaultUserSpecificDetails details = new DefaultUserSpecificDetails();
        details.setIdentity(getIdentity());
        details.setUsername(getUsername());
        details.setCaption(getCaption());
        details.setAuthorities(getAuthorities());
        details.setEnabled(!isDisabled());
        details.setAccountNonExpired(details.isEnabled());
        details.setAccountNonLocked(details.isEnabled());
        details.setCredentialsNonExpired(details.isEnabled());
        return details;
    }

}
