package org.truenewx.tnxjee.repo.lucene.support;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.IndexedUnity;

/**
 * 基于Lucene的被索引单体数据仓库支持
 *
 * @param <T> 被索引单体类型
 * @author jianglei
 */
public abstract class LuceneIndexedUnityRepoSupport<T extends IndexedUnity<K>, K extends Serializable>
        extends LuceneIndexRepoSupport<T> {

    @Override
    protected final String getKeyPropertyName() {
        return "id";
    }

    @Override
    protected final String getDefaultPropertyName() {
        return "content";
    }

}
