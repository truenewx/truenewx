package org.truenewx.tnxsample.admin.repo;

import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxsample.admin.model.entity.ActionLog;
import org.truenewx.tnxsample.admin.model.query.ActionLogQuerying;

/**
 * 操作日志Repo扩展
 *
 * @author jianglei
 */
public interface ActionLogRepox {

    QueryResult<ActionLog> query(ActionLogQuerying querying);

}
