package org.truenewx.tnxjee.test.service.data;

import java.util.List;

import org.truenewx.tnxjee.model.entity.Entity;

/**
 * 
 * @author jianglei
 */
public interface DataPool {

    <T extends Entity> List<T> getDataList(Class<T> entityClass);

}