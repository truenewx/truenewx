package org.truenewx.tnxsample.admin.service.model;

import org.truenewx.tnxjee.model.CommandModel;
import org.truenewx.tnxsample.admin.model.entity.Manager;

/**
 * 管理员自身的命令模型
 */
public class ManagerSelfCommand implements CommandModel<Manager> {
    private String headImageUrl;
    private String fullName;

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
}
