package org.truenewx.tnxjee.repo.jpa.support;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.repo.jpa.hibernate.MetadataProvider;
import org.truenewx.tnxjee.repo.support.DataAccessTemplate;
import org.truenewx.tnxjee.repo.util.RepoUtil;

/**
 * JPA的数据访问模板
 *
 * @author jianglei
 */
public class JpaAccessTemplate implements DataAccessTemplate {

    private String schema = RepoUtil.DEFAULT_SCHEMA_NAME;
    private EntityManagerFactory entityManagerFactory;
    private MetadataProvider metadataProvider;
    private boolean nativeMode;
    private int maxPageSize = 200;

    public JpaAccessTemplate(EntityManagerFactory entityManagerFactory, MetadataProvider metadataProvider) {
        Assert.notNull(entityManagerFactory, "entityManagerFactory must not be null");
        Assert.notNull(metadataProvider, "metadataProvider must not be null");
        this.entityManagerFactory = entityManagerFactory;
        this.metadataProvider = metadataProvider;
    }

    public JpaAccessTemplate(String schema, EntityManagerFactory entityManagerFactory,
            MetadataProvider metadataProvider) {
        this(entityManagerFactory, metadataProvider);
        Assert.notNull(schema, "schema must not be null");
        this.schema = schema;
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = Math.min(maxPageSize, 1000); // 最大允许每页1000条
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public Iterable<Class<?>> getEntityClasses() {
        List<Class<?>> entityClasses = new ArrayList<>();
        getEntityManagerFactory().getMetamodel().getManagedTypes().forEach(type -> {
            entityClasses.add(type.getJavaType());
        });
        return entityClasses;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    public EntityManager getEntityManager() {
        // 每次都重新获取，以免获取的实体管理器在事务会话已关闭后还继续使用，在同一个事务会话中有缓存，无需担心性能
        // 如果此处报NullPointerException，请检查方法调用链上是否有事务注解
        return EntityManagerFactoryUtils.getTransactionalEntityManager(getEntityManagerFactory());
    }

    public PersistentClass getPersistentClass(String entityName) {
        Metadata metadata = this.metadataProvider.getMetadata();
        return metadata == null ? null : metadata.getEntityBinding(entityName);
    }

    private String getTableName(String entityName) {
        PersistentClass persistentClass = getPersistentClass(entityName);
        if (persistentClass != null) {
            return persistentClass.getTable().getName();
        }
        return null;
    }

    /**
     * 创建对应的原生SQL方式的访问模板
     *
     * @return 原生SQL方式的访问模板
     */
    public JpaAccessTemplate createNative() {
        JpaAccessTemplate template = new JpaAccessTemplate(this.schema, getEntityManagerFactory(),
                this.metadataProvider);
        template.nativeMode = true;
        return template;
    }

    public void flush() {
        getEntityManager().flush();
    }

    public void refresh(Entity entity) {
        getEntityManager().refresh(entity);
    }

    /**
     * 非分页查询
     */
    public <T> List<T> list(CharSequence ql, String paramName, Object paramValue) {
        return list(ql, paramName, paramValue, 0, 0);
    }

    /**
     * 非分页查询
     */
    public <T> List<T> list(CharSequence ql, Map<String, ?> params) {
        return list(ql, params, 0, 0);
    }

    /**
     * 非分页查询
     */
    public <T> List<T> list(CharSequence ql, List<?> params) {
        return list(ql, params, 0, 0);
    }

    /**
     * 非分页查询
     */
    public <T> List<T> list(CharSequence ql) {
        return list(ql, (Map<String, ?>) null);
    }

    /**
     * 从多个实体或表中查询清单并简单汇总。由于从多个实体的查询结果合并而来，无法保证结果的排序，调用者如果关注顺序，需对结果重新排序
     *
     * @param qlFormat    包含一个%s作为实体名或表名占位符的查询语句格式，必须对指定的所有实体或表均有效
     * @param params      查询参数
     * @param entityNames 实体或表名清单
     * @param converter   结果转换函数，将每一个查询结果对象转换为想要的结果类型，为null时不进行转换
     * @return 汇总清单
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> list(CharSequence qlFormat, Map<String, ?> params, String[] entityNames,
            @Nullable Function<Object, T> converter) {
        String format = qlFormat.toString();
        Assert.isTrue(format.contains("%s"), "The qlFormat must contain '%s'");
        List<T> result = new ArrayList<>();
        for (String entityName : entityNames) {
            String ql = formatQl(format, entityName);
            List<Object> list = list(ql, params);
            for (Object obj : list) {
                if (converter != null) {
                    obj = converter.apply(obj);
                    if (obj == null) {
                        continue;
                    }
                }
                result.add((T) obj);
            }
        }
        return result;
    }

    private String formatQl(String qlFormat, String entityName) {
        if (this.nativeMode) { // 原生模式，实体名称转换为表名
            entityName = getTableName(entityName);
        }
        return String.format(qlFormat, entityName);
    }

    public <T> T first(CharSequence ql, String paramName, Object paramValue) {
        List<T> list = list(ql, paramName, paramValue, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public <T> T first(CharSequence ql, Map<String, ?> params) {
        List<T> list = list(ql, params, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public <T> T first(CharSequence ql, List<?> params) {
        List<T> list = list(ql, params, 1, 1);
        return CollectionUtil.getFirst(list, null);
    }

    public <T> T first(CharSequence ql) {
        return first(ql, (Map<String, ?>) null);
    }

    @SuppressWarnings("unchecked")
    public <T> T first(CharSequence qlFormat, Map<String, ?> params, String[] entityNames,
            @Nullable Function<Object, T> converter) {
        String format = qlFormat.toString();
        Assert.isTrue(format.contains("%s"), "The qlFormat must contain '%s'");
        for (String entityName : entityNames) {
            String ql = formatQl(format, entityName);
            Object obj = first(ql, params);
            if (obj != null) {
                if (converter != null) {
                    obj = converter.apply(obj);
                }
                if (obj != null) {
                    return (T) obj;
                }
            }
        }
        return null;
    }

    public long count(CharSequence ql, String paramName, Object paramValue) {
        Number value = first(ql, paramName, paramValue);
        return value == null ? 0 : value.longValue();
    }

    public long count(CharSequence ql, Map<String, ?> params) {
        Number value = first(ql, params);
        return value == null ? 0 : value.longValue();
    }

    public long count(CharSequence ql, List<?> params) {
        Number value = first(ql, params);
        return value == null ? 0 : value.longValue();
    }

    public long count(CharSequence ql) {
        return count(ql, (Map<String, ?>) null);
    }

    /**
     * 从多个实体或表中获取汇总总数
     *
     * @param qlFormat    包含一个%s作为实体名或表名占位符的查询语句格式，必须对指定的所有实体或表均有效
     * @param params      查询参数
     * @param entityNames 实体或表名清单
     * @return 总数
     */
    public long count(CharSequence qlFormat, Map<String, ?> params, String[] entityNames) {
        String format = qlFormat.toString();
        Assert.isTrue(format.contains("%s"), "The qlFormat must contain '%s'");
        long count = 0;
        for (String entityName : entityNames) {
            String ql = formatQl(format, entityName);
            count += count(ql, params);
        }
        return count;
    }

    private Query createQuery(CharSequence ql) {
        if (this.nativeMode) {
            return getEntityManager().createNativeQuery(ql.toString());
        } else {
            return getEntityManager().createQuery(ql.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(CharSequence ql, String paramName, Object paramValue, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamToQuery(query, paramName, paramValue);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(CharSequence ql, Map<String, ?> params, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(CharSequence ql, List<?> params, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.getResultList();
    }

    /**
     * 分页查询
     */
    public <T> List<T> list(CharSequence ql, int pageSize, int pageNo) {
        return list(ql, (Map<String, ?>) null, pageSize, pageNo);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listWithOneMore(CharSequence ql, String paramName, Object paramValue, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamToQuery(query, paramName, paramValue);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listWithOneMore(CharSequence ql, Map<String, ?> params, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listWithOneMore(CharSequence ql, List<?> params, int pageSize, int pageNo) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.getResultList();
    }

    /**
     * 分页查询，比指定的页大小多查出一条记录来，用于判断是否还有更多的记录
     *
     * @param ql       查询语句
     * @param pageSize 页大小
     * @param pageNo   页码
     * @return 查询结果
     */
    public <T> List<T> listWithOneMore(CharSequence ql, int pageSize, int pageNo) {
        return listWithOneMore(ql, (Map<String, ?>) null, pageSize, pageNo);
    }

    public int update(CharSequence ql, String paramName, Object paramValue) {
        Query query = createQuery(ql);
        applyParamToQuery(query, paramName, paramValue);
        return query.executeUpdate();
    }

    public int update(CharSequence ql, Map<String, ?> params) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        return query.executeUpdate();
    }

    public int update(CharSequence ql, List<?> params) {
        Query query = createQuery(ql);
        applyParamsToQuery(query, params);
        return query.executeUpdate();
    }

    public int update(CharSequence ul) {
        return update(ul, (Map<String, ?>) null);
    }

    /**
     * 在多个实体或表上执行更新语句
     *
     * @param qlFormat    包含一个%s作为实体名或表名占位符的查询语句格式，必须对指定的所有实体或表均有效
     * @param params      执行参数
     * @param entityNames 实体或表清单
     * @param all         是否在全部实体或表上都执行一遍，false-一旦执行到有影响记录的语句，则停止执行后续语句
     * @return 影响的记录数
     */
    public int update(CharSequence qlFormat, Map<String, ?> params, String[] entityNames, boolean all) {
        String format = qlFormat.toString();
        Assert.isTrue(format.contains("%s"), "The qlFormat must contain '%s'");
        int count = 0;
        for (String entityName : entityNames) {
            String ql = formatQl(format, entityName);
            count += update(ql, params);
            if (!all && count > 0) { // 如果不是全部执行且当前执行的语句有影响记录，则返回当前语句执行结果
                break;
            }
        }
        return count;
    }

    public void applyParamsToQuery(Query query, Map<String, ?> params) {
        if (params != null) {
            for (Entry<String, ?> entry : params.entrySet()) {
                applyParamToQuery(query, entry.getKey(), entry.getValue());
            }
        }
    }

    public void applyParamToQuery(Query query, String name, Object value) {
        if (value instanceof Calendar) {
            query.setParameter(name, (Calendar) value, TemporalType.TIMESTAMP);
        } else if (value instanceof Date) {
            query.setParameter(name, (Date) value, TemporalType.TIMESTAMP);
        } else {
            query.setParameter(name, value);
        }
    }

    public void applyParamsToQuery(Query query, List<?> params) {
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                applyParamToQuery(query, i + 1, params.get(i));
            }
        }
    }

    /**
     * 设置查询参数到查询对象中
     *
     * @param query    查询对象
     * @param position 参数位置，从1开始计数
     * @param value    参数值
     */
    public void applyParamToQuery(Query query, int position, Object value) {
        if (value instanceof Calendar) {
            query.setParameter(position, (Calendar) value, TemporalType.TIMESTAMP);
        } else if (value instanceof Date) {
            query.setParameter(position, (Date) value, TemporalType.TIMESTAMP);
        } else {
            query.setParameter(position, value);
        }
    }

    public void applyPagingToQuery(Query query, int pageSize, int pageNo, boolean oneMore) {
        if (pageSize > 0) { // 用页大小判断是否分页查询
            if (pageSize > this.maxPageSize) {
                pageSize = this.maxPageSize;
            }
            if (pageNo <= 0) { // 页码最小为1
                pageNo = 1;
            }
            query.setFirstResult(pageSize * (pageNo - 1));
            query.setMaxResults(oneMore ? (pageSize + 1) : pageSize);
        }
    }

}
