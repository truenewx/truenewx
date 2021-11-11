package org.truenewx.tnxjee.model.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页
 *
 * @author jianglei
 */
public class Pagination implements Serializable {

    private static final long serialVersionUID = 5918877416013992553L;

    private int pageSize;
    private int pageNo = 1;
    private List<FieldOrder> orders;

    public Pagination() {
    }

    public Pagination(int pageSize, int pageNo) {
        setPageSize(pageSize);
        setPageNo(pageNo);
    }

    public Pagination(int pageSize, int pageNo, List<FieldOrder> orders) {
        this(pageSize, pageNo);
        this.orders = orders;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * @param pageSize 页大小。最小为0，表示不分页；小于0的赋值会被强制视为0
     */
    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(pageSize, 0);
    }

    public int getPageNo() {
        return this.pageNo;
    }

    /**
     * @param pageNo 页码。从1开始计数，小于1的赋值会被强制视为1
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo <= 0 ? 1 : pageNo;
    }

    public List<FieldOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(List<FieldOrder> orders) {
        this.orders = orders;
    }

    //////

    /**
     * 如果当前页大小未设定，则设定为指定页大小默认值
     *
     * @param pageSize 页大小默认值
     */
    public void setPageSizeDefault(int pageSize) {
        if (this.pageSize <= 0) {
            this.pageSize = pageSize;
        }
    }

    public boolean isPageable() {
        return getPageSize() > 0;
    }

    public void addOrder(FieldOrder order) {
        if (order != null) {
            if (this.orders == null) {
                this.orders = new ArrayList<>();
            }
            this.orders.removeIf(o -> o.getName().equals(order.getName()));
            this.orders.add(order);
        }
    }

    public void addOrder(String fieldName, boolean desc) {
        addOrder(new FieldOrder(fieldName, desc));
    }

    public void setOrderDefault(String fieldName, boolean desc) {
        if (isEmptyOrders()) {
            addOrder(fieldName, desc);
        }
    }

    public boolean isEmptyOrders() {
        return this.orders == null || this.orders.isEmpty();
    }

    public void changeOrderFieldName(String oldFieldName, String newFieldName) {
        if (this.orders != null) {
            for (FieldOrder order : this.orders) {
                if (order.getName().equals(oldFieldName)) {
                    order.setName(newFieldName);
                    break;
                }
            }
        }
    }

    public void prependOrderFieldNamePrefix(String prefix) {
        if (this.orders != null) {
            for (FieldOrder order : this.orders) {
                String name = order.getName();
                if (!name.startsWith(prefix)) {
                    order.setName(prefix + name);
                }
            }
        }
    }

}
