package org.truenewx.tnxjee.core.util.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * 三元函数，类似二元函数：{@link java.util.function.BiFunction}
 *
 * @param <T> 第一参数类型
 * @param <S> 第二参数类型
 * @param <U> 第三参数类型
 * @param <R> 结果类型
 */
@FunctionalInterface
public interface TrFunction<T, S, U, R> {

    R apply(T t, S s, U u);

    default <V> TrFunction<T, S, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, S s, U u) -> after.apply(apply(t, s, u));
    }

}
