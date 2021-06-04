package org.truenewx.tnxjee.repo.mongo.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.query.FieldOrder;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.QueryIgnoring;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.repo.mongo.util.MongoQueryUtil;
import org.truenewx.tnxjee.repo.support.RepoxSupport;

/**
 * MongoDB数据访问仓库扩展支持
 *
 * @author jianglei
 */
public abstract class MongoRepoxSupport<T extends Entity> extends RepoxSupport<T> {

    @Override
    public String getEntityName() {
        return getAccessTemplate().getMongoOperations().getCollectionName(getEntityClass());
    }

    @Override
    protected MongoAccessTemplate getAccessTemplate() {
        return (MongoAccessTemplate) super.getAccessTemplate();
    }

    private QueryResult<T> query(List<Criteria> criteriaList, QueryIgnoring ignoring, int pageSize, int pageNo,
            List<FieldOrder> orders) {
        Query query = MongoQueryUtil.buildQuery(criteriaList);
        Long total = null;
        if (pageSize > 0 && ignoring != QueryIgnoring.TOTAL) { // 需分页查询且不忽略总数时，才获取总数
            total = getAccessTemplate().count(getEntityClass(), query);
        }
        List<T> records;
        if ((total != null && total == 0) || ignoring == QueryIgnoring.RECORD) {
            records = new ArrayList<>();
        } else {
            records = getAccessTemplate().list(getEntityClass(), query, pageSize, pageNo, orders);
        }
        return QueryResult.of(records, pageSize, pageNo, total, orders);
    }

    protected QueryResult<T> query(List<Criteria> criteriaList, Paging paging) {
        return query(criteriaList, paging.getIgnoring(), paging.getPageSize(), paging.getPageNo(),
                paging.getOrders());
    }

    protected QueryResult<T> query(List<Criteria> criteriaList, int pageSize, int pageNo, FieldOrder... orders) {
        return query(criteriaList, null, pageSize, pageNo, Arrays.asList(orders));
    }

}
