package org.truenewx.tnxjee.model.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.annotation.RequestParamIgnore;

/**
 * 分页查询条件。通过创建子类附带更多的查询条件
 *
 * @author jianglei
 */
public class Querying extends Pagination implements QueryModel, Paging {

    private static final long serialVersionUID = -3979291396866569456L;

    private QueryIgnoring ignoring;

    public Querying() {
    }

    public Querying(int pageSize, int pageNo) {
        super(pageSize, pageNo);
    }

    public Querying(int pageSize, int pageNo, List<FieldOrder> orders) {
        super(pageSize, pageNo, orders);
    }

    @Override
    public QueryIgnoring getIgnoring() {
        return this.ignoring;
    }

    public void setIgnoring(QueryIgnoring ignoring) {
        this.ignoring = ignoring;
        // 在忽略记录时，为了确保执行获取总数的动作，确保页大小>0
        if (ignoring == QueryIgnoring.RECORD && getPageSize() <= 0) {
            setPageSize(20);
        }
    }

    //////

    @Override
    @RequestParamIgnore // 避免RPC请求时传递复杂的集合属性，通过下面的动态orderBy属性实现字段排序传递
    public List<FieldOrder> getOrders() {
        return super.getOrders();
    }

    public void setOrderBy(String orderBy) {
        setOrders(null);
        if (StringUtils.isNotBlank(orderBy)) {
            String[] orders = orderBy.split(Strings.COMMA);
            for (String order : orders) {
                FieldOrder fieldOrder = FieldOrder.of(order);
                if (fieldOrder != null) {
                    addOrder(fieldOrder);
                }
            }
        }
    }

    public String getOrderBy() {
        return StringUtil.ifBlank(toOrderBy(getOrders()), null);
    }

    /**
     * 将指定查询排序序列转换排序语句，不含order by<br>
     * 如果无排序设置，则返回空字符串
     *
     * @param orders 查询排序序列
     * @return 排序语句
     */
    public static String toOrderBy(Collection<FieldOrder> orders) {
        StringBuilder orderBy = new StringBuilder();
        if (orders != null) {
            Set<String> fieldNames = new HashSet<>();
            for (FieldOrder order : orders) {
                String fieldName = order.getName();
                if (fieldNames.add(fieldName)) { // 忽略重复的字段
                    orderBy.append(Strings.COMMA).append(order);
                } else { // 重复的字段输出警告日志
                    LogUtil.warn(Querying.class, "Repeated field({}) order is ignored.", fieldName);
                }
            }
            if (orderBy.length() > 0) {
                orderBy.deleteCharAt(0); // 去掉首位的多余逗号
            }
        }
        return orderBy.toString();
    }

}
