package org.truenewx.tnxjee.repo.lucene.support;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.OwnedIndexUnity;

/**
 * 基于Lucene的从属索引单体数据仓库支持
 *
 * @param <T> 索引单体类型
 * @param <K> 标识类型
 * @param <O> 所属者类型
 * @author jianglei
 */
public abstract class LuceneOwnedIndexUnityRepoSupport<T extends OwnedIndexUnity<K, O>, K extends Serializable, O extends Serializable>
        extends LuceneOwnedIndexRepoSupport<T, O> {

    @Override
    protected final String getKeyPropertyName() {
        return "id";
    }

    @Override
    protected final String getDefaultPropertyName() {
        return "content";
    }

}
