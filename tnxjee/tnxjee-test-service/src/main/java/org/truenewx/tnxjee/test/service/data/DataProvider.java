package org.truenewx.tnxjee.test.service.data;

import java.util.List;

import org.truenewx.tnxjee.model.entity.Entity;

/**
 * 数据提供者
 *
 * @author jianglei
 */
public interface DataProvider<T extends Entity> {

    /**
     * 如果已有数据则直接返回数据，否则构建数据并返回
     *
     * @param pool 数据池，用于让当前数据提供者获取依赖数据
     * @return 数据清单
     */
    List<T> getDataList(DataPool pool);

    /**
     * 清除所有数据
     */
    void clear();

}
