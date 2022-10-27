package org.truenewx.tnxjee.core.util.function;

import java.util.Objects;

/**
 * 三元断言，类似二元断言：{@link java.util.function.BiPredicate}
 *
 * @param <T> 第一参数类型
 * @param <S> 第二参数类型
 * @param <U> 第三参数类型
 */
@FunctionalInterface
public interface TrPredicate<T, S, U> {

    boolean test(T t, S s, U u);

    default TrPredicate<T, S, U> and(TrPredicate<? super T, ? super S, ? super U> other) {
        Objects.requireNonNull(other);
        return (T t, S s, U u) -> test(t, s, u) && other.test(t, s, u);
    }

    default TrPredicate<T, S, U> negate() {
        return (T t, S s, U u) -> !test(t, s, u);
    }

    default TrPredicate<T, S, U> or(TrPredicate<? super T, ? super S, ? super U> other) {
        Objects.requireNonNull(other);
        return (T t, S s, U u) -> test(t, s, u) || other.test(t, s, u);
    }

}
