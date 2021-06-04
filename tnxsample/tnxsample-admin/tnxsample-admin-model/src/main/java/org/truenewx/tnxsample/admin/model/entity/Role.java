package org.truenewx.tnxsample.admin.model.entity;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotBlank;

import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.validation.constraint.NotContainsSpecialChars;

/**
 * 角色
 *
 * @author jianglei
 */
@Caption("角色")
public class Role implements Unity<Integer>, Comparable<Role> {

    private Integer id;

    @Caption("名称")
    @NotBlank
    @NotContainsSpecialChars
    private String name;

    @Caption("备注")
    @NotContainsSpecialChars
    private String remark;

    @Caption("序号")
    private long ordinal;

    @Caption("权限集")
    private Set<String> permissions;

    /**
     * 管理员集
     */
    private Collection<Manager> managers = new TreeSet<>();

    @Override
    public Integer getId() {
        return this.id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(long ordinal) {
        this.ordinal = ordinal;
    }

    public Set<String> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Collection<Manager> getManagers() {
        return this.managers;
    }

    protected void setManagers(Collection<Manager> managers) {
        this.managers = managers;
    }

    //////

    @Override
    public int compareTo(Role other) {
        return Long.compare(getOrdinal(), other.getOrdinal());
    }

}
