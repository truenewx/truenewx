package org.truenewx.tnxjee.model.query;

import java.util.*;
import java.util.function.Function;

/**
 * 分页查询结果
 *
 * @param <T> 结果记录类型
 * @author jianglei
 */
public class QueryResult<T> implements Iterable<T> {

    private List<T> records;
    private Paged paged;

    protected QueryResult() {
    }

    public QueryResult(List<T> records, Paged paged) {
        this.records = Objects.requireNonNullElse(records, Collections.emptyList());
        this.paged = paged;
    }

    public static <T> QueryResult<T> of(List<T> records, int pageSize, int pageNo, Long total,
            List<FieldOrder> orders) {
        if (pageSize <= 0) {
            pageSize = records.size();
            pageNo = 1;
        }
        Paged paged;
        if (total != null) {
            paged = new Paged(pageSize, pageNo, total);
        } else {
            boolean morePage = records.size() > pageSize;
            while (records.size() > pageSize) { // 确保结果数据数目不大于页大小
                records.remove(records.size() - 1);
            }
            paged = new Paged(pageSize, pageNo, morePage);
        }
        paged.setOrders(orders);
        return new QueryResult<>(records, paged);
    }

    public static <T> QueryResult<T> empty(Pagination pagination) {
        return new QueryResult<>(null, Paged.of(pagination, 0));
    }

    public List<T> getRecords() {
        return this.records;
    }

    public Paged getPaged() {
        return this.paged;
    }

    @Override
    public Iterator<T> iterator() {
        return this.records.iterator();
    }

    public <R> QueryResult<R> map(Function<T, R> function) {
        List<R> list = new ArrayList<>();
        this.records.forEach(record -> {
            list.add(function.apply(record));
        });
        return new QueryResult<>(list, this.paged);
    }

}
