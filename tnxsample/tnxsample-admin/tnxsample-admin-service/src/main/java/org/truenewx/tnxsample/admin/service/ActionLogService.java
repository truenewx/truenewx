package org.truenewx.tnxsample.admin.service;

import java.time.LocalDate;

import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.service.unity.UnityService;
import org.truenewx.tnxsample.admin.model.entity.Action;
import org.truenewx.tnxsample.admin.model.entity.ActionLog;

/**
 * 操作日志服务
 *
 * @author jianglei
 */
public interface ActionLogService extends UnityService<ActionLog, String> {

    void add(int managerId, Action action);

    QueryResult<ActionLog> query(String managerKeyword, LocalDate beginDate, LocalDate endDate,
            int pageSize, int pageNo);

}
