package org.truenewx.tnxjee.model.spec;

/**
 * 可排序的
 */
public interface Sortable {

    /**
     * @return 排序号
     */
    long getOrdinal();

    /**
     * @param ordinal 排序号
     */
    void setOrdinal(long ordinal);

}
