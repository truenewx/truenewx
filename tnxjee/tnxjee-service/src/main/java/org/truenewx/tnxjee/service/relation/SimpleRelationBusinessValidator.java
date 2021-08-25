package org.truenewx.tnxjee.service.relation;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.relation.Relation;

/**
 * 通过简单关系传递数据的关系业务逻辑校验器<br>
 * 字段格式校验由格式校验框架完成，本接口的实现仅负责通过读取持久化数据验证字段数据的业务逻辑合法性
 *
 * @author jianglei
 */
public interface SimpleRelationBusinessValidator<T extends Relation<L, R>, L extends Serializable, R extends Serializable> {

    /**
     * 验证指定id的指定单体数据的业务逻辑合法性
     *
     * @param leftId   关系左标识
     * @param rightId  关系右标识
     * @param relation 关系数据，不是从数据库读取的单体，不包含标识
     */
    // 方法名中含有Business字样，是为了凸显验证业务逻辑而不是格式，同时也减少与其它方法重名的可能性
    void validateBusiness(L leftId, R rightId, T relation);

}
