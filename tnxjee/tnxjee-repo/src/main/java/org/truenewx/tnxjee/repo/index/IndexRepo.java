package org.truenewx.tnxjee.repo.index;

/**
 * 索引数据访问仓库
 *
 * @param <T> 实体类型
 * @author jianglei
 */
public interface IndexRepo<T> {

    void save(T object);

    void delete(T object);

}
