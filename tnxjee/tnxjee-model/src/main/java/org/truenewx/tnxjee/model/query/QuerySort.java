package org.truenewx.tnxjee.model.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

/**
 * 查询排序
 *
 * @author jianglei
 */
@Deprecated
public class QuerySort implements Serializable {

    private static final long serialVersionUID = 1091207925288843577L;

    private List<FieldOrder> orders;

    public QuerySort() {
    }

    public QuerySort(List<FieldOrder> orders) {
        this.orders = orders;
    }

    public static QuerySort of(FieldOrder... orders) {
        return new QuerySort(Arrays.asList(orders));
    }

    public static QuerySort of(SortedMap<String, Boolean> orderMap) {
        QuerySort sort = new QuerySort();
        orderMap.forEach((fieldName, desc) -> {
//            sort.addOrder(fieldName, desc);
        });
        return sort;
    }

    public static QuerySort of(String fieldName, boolean desc) {
        return of(new FieldOrder(fieldName, desc));
    }

    public List<FieldOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(List<FieldOrder> orders) {
        this.orders = orders;
    }

    public List<FieldOrder> getOrders(boolean notNull) {
        if (notNull && this.orders == null) {
            this.orders = new ArrayList<FieldOrder>();
        }
        return this.orders;
    }

}
