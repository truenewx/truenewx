package org.truenewx.tnxsample.admin.service.model;

import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxsample.admin.model.entity.Manager;

/**
 * 管理员命令模型
 *
 * @author jianglei
 */
public class ManagerCommand implements CommandModel<Manager> {

    private String jobNo;
    private String username;
    private String password;
    private String fullName;
    private int[] roleIds;

    public String getJobNo() {
        return this.jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

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

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int[] getRoleIds() {
        return this.roleIds;
    }

    public void setRoleIds(int[] roleIds) {
        this.roleIds = roleIds;
    }

}
