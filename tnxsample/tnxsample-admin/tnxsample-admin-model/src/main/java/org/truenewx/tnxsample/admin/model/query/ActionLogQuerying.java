package org.truenewx.tnxsample.admin.model.query;

import java.time.Instant;
import java.util.List;

import org.truenewx.tnxjee.model.query.Querying;

/**
 * 操作日志的分页查询条件
 *
 * @author jianglei
 */
public class ActionLogQuerying extends Querying {

    private static final long serialVersionUID = -1417419280956901061L;

    private List<Integer> managerIds;
    private Instant beginTime;
    private Instant endTime;

    public List<Integer> getManagerIds() {
        return this.managerIds;
    }

    public void setManagerIds(List<Integer> managerIds) {
        this.managerIds = managerIds;
    }

    public Instant getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(Instant beginTime) {
        this.beginTime = beginTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

}
