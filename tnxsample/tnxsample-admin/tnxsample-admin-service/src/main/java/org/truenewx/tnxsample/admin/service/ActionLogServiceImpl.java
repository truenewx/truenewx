package org.truenewx.tnxsample.admin.service;

import java.time.Instant;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.core.util.TemporalUtil;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.service.impl.unity.AbstractUnityService;
import org.truenewx.tnxsample.admin.model.entity.Action;
import org.truenewx.tnxsample.admin.model.entity.ActionLog;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.model.query.ActionLogQuerying;
import org.truenewx.tnxsample.admin.repo.ActionLogRepo;
import org.truenewx.tnxsample.admin.repo.ManagerRepox;
import org.truenewx.tnxsample.admin.repo.entity.ActionLogImpl;

/**
 * 操作日志服务实现
 *
 * @author jianglei
 */
@Service
public class ActionLogServiceImpl extends AbstractUnityService<ActionLog, String>
        implements ActionLogService {

    @Autowired
    private ActionLogRepo repo;
    @Autowired
    private ManagerRepox managerRepo;

    @Override
    public void add(int managerId, Action action) {
        Manager manager = getService(ManagerService.class).find(managerId);
        if (manager != null) {
            ActionLog log = new ActionLogImpl();
            log.setManagerId(managerId);
            log.setAction(action);
            log.setCreateTime(Instant.now());
            this.repo.save(log);
        }
    }

    @Override
    public QueryResult<ActionLog> query(String managerKeyword, LocalDate beginDate,
            LocalDate endDate, int pageSize, int pageNo) {
        ActionLogQuerying querying = new ActionLogQuerying();
        if (StringUtils.isNotBlank(managerKeyword)) {
            querying.setManagerIds(this.managerRepo.getIdsByUsernameOrFullName(managerKeyword));
        }
        if (beginDate != null) {
            querying.setBeginTime(TemporalUtil.toInstant(beginDate));
        }
        if (endDate != null) {
            querying.setEndTime(TemporalUtil.toInstant(endDate.plusDays(1)));
        }
        querying.setPageSize(pageSize);
        querying.setPageNo(pageNo);
        querying.addOrder("createTime", true);
        return this.repo.query(querying);
    }

}
