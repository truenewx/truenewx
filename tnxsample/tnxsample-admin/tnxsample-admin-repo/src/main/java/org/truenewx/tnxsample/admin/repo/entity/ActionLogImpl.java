package org.truenewx.tnxsample.admin.repo.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.truenewx.tnxsample.admin.model.entity.Action;
import org.truenewx.tnxsample.admin.model.entity.ActionLog;

/**
 * 操作日志实现
 *
 * @author jianglei
 */
public class ActionLogImpl implements ActionLog {

    @Id
    @Field("_id")
    private String id;
    private int managerId;
    private Instant createTime;
    private Action action;

    @Override
    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    @Override
    public int getManagerId() {
        return this.managerId;
    }

    @Override
    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    @Override
    public Instant getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    @Override
    public Action getAction() {
        return this.action;
    }

    @Override
    public void setAction(Action action) {
        this.action = action;
    }

}
