package org.truenewx.tnxjee.model.query;

import java.util.List;

/**
 * ๅ้กต็
 */
public interface Paging {

    int getPageSize();

    int getPageNo();

    List<FieldOrder> getOrders();

    QueryIgnoring getIgnoring();

}
