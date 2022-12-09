package org.truenewx.tnxjee.repo.jpa.support;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.query.*;
import org.truenewx.tnxjee.repo.jpa.JpaRepox;
import org.truenewx.tnxjee.repo.jpa.util.DataExportingTable;
import org.truenewx.tnxjee.repo.jpa.util.OqlUtil;
import org.truenewx.tnxjee.repo.support.RepoxSupport;
import org.truenewx.tnxjee.repo.util.ModelPropertyLimitValueManager;

/**
 * JPA的数据访问仓库扩展支持
 *
 * @author jianglei
 */
public abstract class JpaRepoxSupport<T extends Entity> extends RepoxSupport<T> implements JpaRepox<T> {

    @Autowired
    private ModelPropertyLimitValueManager propertyLimitValueManager;

    @Override
    protected JpaAccessTemplate getAccessTemplate() {
        return (JpaAccessTemplate) super.getAccessTemplate();
    }

    @Override
    public String getEntityName() {
        return getEntityClass().getName();
    }

    @Override
    public void flush() {
        getAccessTemplate().flush();
    }

    @Override
    public void refresh(T entity) {
        getAccessTemplate().refresh(entity);
    }

    protected <E> QueryResult<E> query(CharSequence recordQl, CharSequence totalQl, Map<String, Object> params,
            int pageSize, int pageNo, List<FieldOrder> orders) {
        Long total = null;
        if (pageSize > 0 && totalQl != null) {
            total = getAccessTemplate().count(totalQl, params);
        }

        List<E> records;
        // 已知总数为0或无需查询记录清单，则不查询记录清单
        if ((total != null && total == 0) || recordQl == null) {
            records = new ArrayList<>();
        } else {
            String orderString = OqlUtil.buildOrderString(orders);
            if (StringUtils.isNotBlank(orderString)) {
                if (recordQl instanceof StringBuilder) {
                    ((StringBuilder) recordQl).append(orderString);
                } else if (recordQl instanceof StringBuffer) {
                    ((StringBuffer) recordQl).append(orderString);
                } else {
                    recordQl = recordQl + orderString;
                }
            }

            // 分页查询但未指定取数语句，未取总数，则多查询一条记录，以判断是否还有更多数据
            if (pageSize > 0 && totalQl == null) {
                records = getAccessTemplate().listWithOneMore(recordQl, params, pageSize, pageNo);
                boolean morePage = records.size() > pageSize;
                if (morePage) {
                    records.remove(records.size() - 1);
                }
                return new QueryResult<>(records, new Paged(pageSize, pageNo, morePage));
            }

            records = getAccessTemplate().list(recordQl, params, pageSize, pageNo);
            if (pageSize <= 0) { // 非分页查询，总数为结果记录条数
                total = (long) records.size();
            }
        }
        return QueryResult.of(records, pageSize, pageNo, total, orders);
    }

    protected <E> QueryResult<E> query(CharSequence ql, Map<String, Object> params, int pageSize, int pageNo,
            List<FieldOrder> orders, QueryIgnoring ignoring) {
        String totalQl = null;
        if (pageSize > 0 && ignoring != QueryIgnoring.TOTAL) {
            totalQl = ql.toString();
            totalQl = "select count(*) " + totalQl.substring(totalQl.indexOf("from "));
        }
        if (ignoring == QueryIgnoring.RECORD) {
            ql = null;
        }
        return query(ql, totalQl, params, pageSize, pageNo, orders);
    }

    protected <E> QueryResult<E> query(CharSequence ql, Map<String, Object> params, Paging paging) {
        if (paging == null) {
            return query(ql, params, 0, 1, null, null);
        }
        return query(ql, params, paging.getPageSize(), paging.getPageNo(), paging.getOrders(), paging.getIgnoring());
    }

    protected <E> QueryResult<E> query(CharSequence ql, Map<String, Object> params, int pageSize, int pageNo,
            FieldOrder... orders) {
        return query(ql, params, pageSize, pageNo, Arrays.asList(orders), null);
    }

    protected final String getTableName() {
        return getAccessTemplate().getTableName(getEntityName());
    }

    private PersistentClass getPersistentClass() {
        return getAccessTemplate().getPersistentClass(getEntityName());
    }

    private List<String> getColumnNames() {
        SessionFactory sessionFactory = getAccessTemplate().getEntityManagerFactory().unwrap(SessionFactory.class);
        Session session = sessionFactory.openSession();
        List<String> columnNames = session.doReturningWork(connection -> {
            List<String> fieldNames = new ArrayList<>();
            ResultSet rs = connection.getMetaData().getColumns(connection.getCatalog(), null, getTableName(), null);
            while (rs.next()) {
                fieldNames.add(rs.getString(4));
            }
            rs.close();
            return fieldNames;
        });
        session.close();
        return columnNames;
    }

    protected final Column getColumn(String propertyName) {
        Property property = getPersistentClass().getProperty(propertyName);
        return (Column) property.getColumnIterator().next();
    }

    protected final String getColumnName(String propertyName) {
        return getColumn(propertyName).getName();
    }

    private Number getNumberPropertyMinValue(String propertyName) {
        Class<T> entityClass = getEntityClass();
        if (this.propertyLimitValueManager.isNonNumber(entityClass, propertyName)) {
            // 已明确知晓不是数字类型的属性，直接返回null
            return null;
        }
        Number minValue = this.propertyLimitValueManager.getMinValue(entityClass, propertyName);
        if (minValue == null) {
            Class<?> propertyClass = getPropertyClass(propertyName);
            minValue = MathUtil.minValue(propertyClass);
            if (minValue != null) { // 可从类型取得最小值，说明是数值类型
                Min min = ClassUtil.findAnnotation(getEntityClass(), propertyName, Min.class);
                if (min != null) {
                    minValue = min.value();
                } else {
                    DecimalMin decimalMin = ClassUtil.findAnnotation(getEntityClass(), propertyName, DecimalMin.class);
                    if (decimalMin != null) {
                        minValue = MathUtil.parseDecimal(decimalMin.value(), null);
                    }
                }

                @SuppressWarnings("unchecked")
                Class<? extends Number> type = (Class<? extends Number>) propertyClass;
                Column column = getColumn(propertyName);
                int precision = column.getPrecision();
                int scale = column.getScale();
                Number minValue2 = MathUtil.minValue(type, precision, scale);
                // 两个最小值中的较大者，才是实际允许的最小值
                if (minValue2 != null && minValue2.doubleValue() > minValue.doubleValue()) {
                    minValue = minValue2;
                }
            }
            this.propertyLimitValueManager.putMinValue(entityClass, propertyName, minValue);
        }
        return minValue;
    }

    private Number getNumberPropertyMaxValue(String propertyName) {
        Class<T> entityClass = getEntityClass();
        if (this.propertyLimitValueManager.isNonNumber(entityClass, propertyName)) {
            // 已明确知晓不是数字类型的属性，直接返回null
            return null;
        }
        Number maxValue = this.propertyLimitValueManager.getMaxValue(entityClass, propertyName);
        if (maxValue == null) {
            Class<?> propertyClass = getPropertyClass(propertyName);
            maxValue = MathUtil.maxValue(propertyClass);
            if (maxValue != null) { // 可从类型取得最大值，说明是数值类型
                Max max = ClassUtil.findAnnotation(getEntityClass(), propertyName, Max.class);
                if (max != null) {
                    maxValue = max.value();
                } else {
                    DecimalMax decimalMax = ClassUtil.findAnnotation(getEntityClass(), propertyName, DecimalMax.class);
                    if (decimalMax != null) {
                        maxValue = MathUtil.parseDecimal(decimalMax.value(), null);
                    }
                }

                @SuppressWarnings("unchecked")
                Class<? extends Number> type = (Class<? extends Number>) propertyClass;
                Column column = getColumn(propertyName);
                int precision = column.getPrecision();
                int scale = column.getScale();
                Number maxValue2 = MathUtil.maxValue(type, precision, scale);
                // 两个最大值中的较小者，才是实际允许的最大值
                if (maxValue2 != null && maxValue2.doubleValue() < maxValue.doubleValue()) {
                    maxValue = maxValue2;
                }
            }
            this.propertyLimitValueManager.putMaxValue(entityClass, propertyName, maxValue);
        }
        return maxValue;
    }

    protected final boolean doIncreaseNumber(StringBuilder ql, Map<String, Object> params, String propertyName,
            boolean positive, Number limit) {
        if (positive) { // 增量为正时需限定最大值
            Number maxValue = limit == null ? getNumberPropertyMaxValue(propertyName) : limit;
            if (maxValue == null) { // 无法取得最大限定值，则不执行更新操作
                return false;
            }
            ql.append(" and ").append(propertyName).append("+:step<=:maxValue");
            params.put("maxValue", maxValue);
        } else { // 增量为负时需限定最小值
            Number minValue = limit == null ? getNumberPropertyMinValue(propertyName) : limit;
            if (minValue == null) { // 无法取得最小限定值，则不执行更新操作
                return false;
            }
            ql.append(" and ").append(propertyName).append("+:step>=:minValue");
            params.put("minValue", minValue);
        }
        return getAccessTemplate().update(ql, params) > 0;
    }

    protected final DataExportingTable exportData(String alias, CharSequence followedSql, Map<String, Object> params,
            int pageSize, int pageNo) {
        return exportData(alias, followedSql, (table, sql) -> {
            List<Object[]> records = getAccessTemplate().createNative().listWithOneMore(sql, params, pageSize, pageNo);
            table.setMorePage(records.size() > pageSize);
            if (table.isMorePage()) {
                records.remove(records.size() - 1);
            }
            table.setRecords(records);
        });
    }

    protected final DataExportingTable exportData(CharSequence followedSql, Map<String, Object> params, int pageSize,
            int pageNo) {
        return exportData(null, followedSql, params, pageSize, pageNo);
    }

    private DataExportingTable exportData(String alias, CharSequence followedSql,
            BiConsumer<DataExportingTable, CharSequence> consumer) {
        List<String> columnNames = getColumnNames();
        DataExportingTable table = new DataExportingTable(getTableName(), columnNames);
        StringBuilder sql = new StringBuilder("select ");
        if (StringUtils.isBlank(alias)) {
            sql.append(StringUtils.join(columnNames, Strings.COMMA));
        } else {
            for (String columnName : columnNames) {
                sql.append(alias).append(Strings.DOT).append(columnName).append(Strings.COMMA);
            }
            sql.deleteCharAt(sql.length() - 1);
        }
        sql.append(" from ").append(table.getTableName()).append(followedSql);
        consumer.accept(table, sql);
        return table;
    }

    protected final DataExportingTable exportData(String alias, CharSequence followedSql, Map<String, Object> params) {
        return exportData(alias, followedSql, (table, sql) -> {
            table.setRecords(getAccessTemplate().createNative().list(sql, params));
        });
    }

    protected final DataExportingTable exportData(CharSequence followedSql, Map<String, Object> params) {
        return exportData(null, followedSql, params);
    }

}
