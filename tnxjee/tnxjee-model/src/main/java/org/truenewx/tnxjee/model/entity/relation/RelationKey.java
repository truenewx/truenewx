package org.truenewx.tnxjee.model.entity.relation;

import java.io.Serializable;

import org.truenewx.tnxjee.core.util.tuple.Binate;

/**
 * 关系的主键类型
 *
 * @author jianglei
 */
public interface RelationKey<L extends Serializable, R extends Serializable> extends Binate<L, R>, Serializable {

}
