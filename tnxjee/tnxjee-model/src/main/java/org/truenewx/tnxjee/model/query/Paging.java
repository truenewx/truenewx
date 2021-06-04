package org.truenewx.tnxjee.model.query;

import java.util.List;

/**
 * 分页的
 */
public interface Paging {

    int getPageSize();

    int getPageNo();

    List<FieldOrder> getOrders();

    QueryIgnoring getIgnoring();

}
