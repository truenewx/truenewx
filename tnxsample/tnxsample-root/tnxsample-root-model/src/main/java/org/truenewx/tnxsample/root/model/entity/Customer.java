package org.truenewx.tnxsample.root.model.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

import javax.validation.constraints.NotBlank;

import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.DefaultUserIdentity;
import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjee.model.spec.user.UserSpecific;
import org.truenewx.tnxjee.model.spec.user.security.DefaultUserSpecificDetails;
import org.truenewx.tnxjee.model.spec.user.security.UserGrantedAuthority;
import org.truenewx.tnxjee.model.validation.constraint.Cellphone;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsSpecialChars;
import org.truenewx.tnxsample.common.constant.AppNames;
import org.truenewx.tnxsample.common.constant.UserTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 客户
 *
 * @author jianglei
 */
@Caption("客户")
public class Customer implements Unity<Integer>, UserSpecific<IntegerUserIdentity> {

    private Integer id;
    @NotBlank
    @Caption("手机号码")
    @Cellphone
    private String cellphone;
    @Caption("密码")
    private String password;
    @NotContainsSpecialChars
    @Caption("昵称")
    private String nickname;
    @Caption("性别")
    private Gender gender;
    @Caption("是否禁用")
    private boolean disabled;
    @Caption("注册时间")
    private Instant createTime;

    @Override
    public Integer getId() {
        return this.id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public String getCellphone() {
        return this.cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public String getType() {
        return UserTypes.CUSTOMER;
    }

    @Override
    @JsonIgnore
    public DefaultUserIdentity getIdentity() {
        return new DefaultUserIdentity(getType(), getId());
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return getCellphone();
    }

    @Override
    @JsonIgnore
    public String getCaption() {
        return getNickname();
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new UserGrantedAuthority(getType(), null, AppNames.ROOT));
    }

    @JsonIgnore
    public DefaultUserSpecificDetails getSpecificDetails() {
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
