package org.truenewx.tnxjee.repo.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 可历史化实体的数据访问扩展仓库助手
 *
 * @param <R> 常规实体和历史实体一致的数据访问扩展仓库接口
 */
public class HistorizableRepoxHelper<R> {

    private R presentRepox;
    private R historyRepo;

    public HistorizableRepoxHelper(R presentRepox, R historyRepox) {
        this.presentRepox = presentRepox;
        this.historyRepo = historyRepox;
    }

    public R getRepox(boolean historized) {
        return historized ? this.historyRepo : this.presentRepox;
    }

    public long count(Function<R, Long> function) {
        long count = function.apply(getRepox(false));
        count += function.apply(getRepox(true));
        return count;
    }

    public <T> T single(boolean historyFirst, Function<R, T> function) {
        T result = function.apply(getRepox(historyFirst));
        if (result == null) {
            result = function.apply(getRepox(!historyFirst));
        }
        return result;
    }

    public <T> List<T> list(Function<R, List<T>> function) {
        List<T> list = function.apply(getRepox(false));
        list.addAll(function.apply(getRepox(true)));
        return list;
    }

    public void loop(boolean historyFirst, Predicate<R> predicate) {
        if (predicate.test(getRepox(historyFirst))) {
            predicate.test(getRepox(!historyFirst));
        }
    }

}
