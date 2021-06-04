package org.truenewx.tnxsample.admin.web.model;

import org.truenewx.tnxsample.admin.model.entity.Manager;

public class LeastManager {

    private Manager unity;
    private String headImageUrl;

    public LeastManager(Manager unity) {
        this.unity = unity;
    }

    public String getCaption() {
        return this.unity.getCaption();
    }

    public String getHeadImageUrl() {
        return this.headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

}
