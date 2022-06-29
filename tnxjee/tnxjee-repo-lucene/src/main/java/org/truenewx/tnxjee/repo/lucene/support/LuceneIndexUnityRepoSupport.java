package org.truenewx.tnxjee.repo.lucene.support;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.IndexUnity;

/**
 * 基于Lucene的索引单体数据仓库支持
 *
 * @param <T> 索引单体类型
 * @param <K> 标识类型
 * @author jianglei
 */
public abstract class LuceneIndexUnityRepoSupport<T extends IndexUnity<K>, K extends Serializable>
        extends LuceneAloneIndexRepoSupport<T> {

    @Override
    protected final String getKeyPropertyName() {
        return "id";
    }

    @Override
    protected final String getDefaultPropertyName() {
        return "content";
    }

}
