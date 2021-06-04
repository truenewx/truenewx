package org.truenewx.tnxsample.admin.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.repo.mongo.support.MongoUnityRepoxSupport;
import org.truenewx.tnxsample.admin.model.entity.ActionLog;
import org.truenewx.tnxsample.admin.model.query.ActionLogQuerying;

/**
 * 操作日志Repo实现
 *
 * @author jianglei
 */
@Repository
public class ActionLogRepoImpl extends MongoUnityRepoxSupport<ActionLog, String> implements ActionLogRepox {

    @Override
    public QueryResult<ActionLog> query(ActionLogQuerying querying) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(querying.getManagerIds())) {
            criteriaList.add(Criteria.where("managerId").in(querying.getManagerIds()));
        }
        if (querying.getBeginTime() != null) {
            criteriaList.add(Criteria.where("createTime").gte(querying.getBeginTime()));
        }
        if (querying.getEndTime() != null) {
            criteriaList.add(Criteria.where("createTime").lt(querying.getEndTime()));
        }

        return query(criteriaList, querying);
    }

}
