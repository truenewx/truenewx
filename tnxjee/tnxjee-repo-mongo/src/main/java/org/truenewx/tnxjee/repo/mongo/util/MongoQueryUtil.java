package org.truenewx.tnxjee.repo.mongo.util;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * MongoDB查询工具类
 *
 * @author jianglei
 */
public class MongoQueryUtil {

    private MongoQueryUtil() {
    }

    /**
     * 用指定Criteria集合以and的形式构建一个Query对象
     *
     * @param criteriaList Criteria集合
     * @return Query对象
     */
    public static Query buildQuery(List<Criteria> criteriaList) {
        Criteria[] array = criteriaList.toArray(new Criteria[0]);
        return new Query(new Criteria().andOperator(array));
    }

}
