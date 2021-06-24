package org.truenewx.tnxjee.repo.jpa.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.model.query.FieldOrder;
import org.truenewx.tnxjee.model.query.Paging;
import org.truenewx.tnxjee.model.query.QueryIgnoring;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.repo.jpa.JpaRepo;
import org.truenewx.tnxjee.repo.jpa.util.OqlUtil;
import org.truenewx.tnxjee.repo.support.RepoxSupport;
import org.truenewx.tnxjee.repo.util.ModelPropertyLimitValueManager;

/**
 * JPA的数据访问仓库扩展支持
 *
 * @author jianglei
 */
public abstract class JpaRepoxSupport<T extends Entity> extends RepoxSupport<T> implements JpaRepo<T> {

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

    protected <E> QueryResult<E> query(CharSequence ql, Map<String, Object> params, QueryIgnoring ignoring,
            int pageSize, int pageNo, List<FieldOrder> orders) {
        Long total = null;
        if (pageSize > 0 && ignoring != QueryIgnoring.TOTAL) { // 需分页查询且不忽略总数时，才获取总数
            String countQl = ql.toString();
            countQl = "select count(*) " + countQl.substring(countQl.indexOf("from "));
            total = getAccessTemplate().count(countQl, params);
        }

        List<E> records;
        // 已知总数为0或无需查询记录清单，则不查询记录清单
        if ((total != null && total == 0) || ignoring == QueryIgnoring.RECORD) {
            records = new ArrayList<>();
        } else {
            String orderString = OqlUtil.buildOrderString(orders);
            if (StringUtils.isNotBlank(orderString)) {
                if (ql instanceof StringBuffer) {
                    ((StringBuffer) ql).append(orderString);
                } else {
                    ql = ql.toString() + orderString;
                }
            }
            records = getAccessTemplate().list(ql, params, pageSize, pageNo);
            if (pageSize <= 0) { // 非分页查询，总数为结果记录条数
                total = (long) records.size();
            }
        }
        return QueryResult.of(records, pageSize, pageNo, total, orders);
    }

    protected <E> QueryResult<E> query(CharSequence ql, Map<String, Object> params, Paging paging) {
        if (paging == null) {
            return query(ql, params, null, 0, 1, null);
        }
        return query(ql, params, paging.getIgnoring(), paging.getPageSize(), paging.getPageNo(), paging.getOrders());
    }

    protected <E> QueryResult<E> query(CharSequence ql, Map<String, Object> params, int pageSize, int pageNo,
            FieldOrder... orders) {
        return query(ql, params, null, pageSize, pageNo, Arrays.asList(orders));
    }

    protected final String getTableName() {
        return getPersistentClass().getTable().getName();
    }

    private PersistentClass getPersistentClass() {
        return getAccessTemplate().getPersistentClass(getEntityName());
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

    protected final boolean doIncreaseNumber(StringBuffer ql, Map<String, Object> params, String propertyName,
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
}
