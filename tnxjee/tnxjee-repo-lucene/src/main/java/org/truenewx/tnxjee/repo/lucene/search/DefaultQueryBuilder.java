package org.truenewx.tnxjee.repo.lucene.search;

import java.math.BigDecimal;
import java.time.temporal.Temporal;

import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 默认的查询构建器
 *
 * @author jianglei
 */
public class DefaultQueryBuilder {

    private BooleanQuery.Builder delegate;

    public DefaultQueryBuilder() {
        this.delegate = new BooleanQuery.Builder();
    }

    public DefaultQueryBuilder add(Query query, BooleanClause.Occur occur) {
        if (query != null) {
            this.delegate.add(query, occur);
        }
        return this;
    }

    public DefaultQueryBuilder add(String name, Object value, BooleanClause.Occur occur) {
        return add(create(name, value), occur);
    }

    /**
     * 添加必须条件，类似AND
     *
     * @param query 条件
     * @return 当前构建器
     */
    public DefaultQueryBuilder must(Query query) {
        return add(query, BooleanClause.Occur.MUST);
    }

    /**
     * 添加必须条件，类似AND
     *
     * @param name  条件字段名
     * @param value 条件字段值
     * @return 当前构建器
     */
    public DefaultQueryBuilder must(String name, Object value) {
        return must(create(name, value));
    }

    /**
     * 添加必须不条件，类似AND NOT
     *
     * @param query 条件
     * @return 当前构建器
     */
    public DefaultQueryBuilder mustNot(Query query) {
        return add(query, BooleanClause.Occur.MUST_NOT);
    }

    /**
     * 添加必须不条件，类似AND NOT
     *
     * @param name  条件字段名
     * @param value 条件字段值
     * @return 当前构建器
     */
    public DefaultQueryBuilder mustNot(String name, Object value) {
        return mustNot(create(name, value));
    }

    /**
     * 添加应该条件，类似OR
     *
     * @param query 条件
     * @return 当前构建器
     */
    public DefaultQueryBuilder should(Query query) {
        return add(query, BooleanClause.Occur.SHOULD);
    }

    /**
     * 添加应该条件，类似OR
     *
     * @param name  条件字段名
     * @param value 条件字段值
     * @return 当前构建器
     */
    public DefaultQueryBuilder should(String name, Object value) {
        return should(create(name, value));
    }

    public BooleanQuery build() {
        return this.delegate.build();
    }


    /**
     * 创建默认查询条件，数值类型值创建精确等于查询条件，其它创建包含字符串匹配查询条件
     *
     * @param name  条件字段名
     * @param value 条件字段值
     * @return 默认查询条件
     */
    public static Query create(String name, Object value) {
        if (value instanceof Long) {
            return LongPoint.newExactQuery(name, (Long) value);
        }
        if (value instanceof Integer) {
            return IntPoint.newExactQuery(name, (Integer) value);
        }
        if (value instanceof BigDecimal) {
            return DoublePoint.newExactQuery(name, ((BigDecimal) value).doubleValue());
        }
        if (value instanceof Double) {
            return DoublePoint.newExactQuery(name, (Double) value);
        }
        if (value instanceof Float) {
            return FloatPoint.newExactQuery(name, (Float) value);
        }
        if (value instanceof Temporal) {
            value = TemporalUtil.format((Temporal) value);
        }
        return new TermQuery(new Term(name, value.toString()));
    }

}
