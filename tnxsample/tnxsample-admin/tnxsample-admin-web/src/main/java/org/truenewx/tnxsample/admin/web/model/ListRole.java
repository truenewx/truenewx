package org.truenewx.tnxsample.admin.web.model;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.entity.Role;

/**
 * 清单中的角色
 */
public class ListRole {

    private Role unity;
    private List<Manager> managers;
    private long managerNum;

    public ListRole(Role unity) {
        this.unity = unity;
    }

    public Integer getId() {
        return this.unity.getId();
    }

    @NotBlank
    public String getName() {
        return this.unity.getName();
    }

    public String getRemark() {
        return this.unity.getRemark();
    }

    public long getOrdinal() {
        return this.unity.getOrdinal();
    }

    public Set<String> getPermissions() {
        return this.unity.getPermissions();
    }

    public List<Manager> getManagers() {
        return this.managers;
    }

    public void setManagers(List<Manager> managers) {
        this.managers = managers;
    }

    public long getManagerNum() {
        return this.managerNum;
    }

    public void setManagerNum(long managerNum) {
        this.managerNum = managerNum;
    }
}
