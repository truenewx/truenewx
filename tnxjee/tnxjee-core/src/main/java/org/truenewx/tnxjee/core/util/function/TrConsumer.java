package org.truenewx.tnxjee.core.util.function;

import java.util.Objects;

/**
 * 三元消费者，类似二元消费者：{@link java.util.function.BiConsumer}
 *
 * @param <T> 第一参数类型
 * @param <S> 第二参数类型
 * @param <U> 第三参数类型
 */
@FunctionalInterface
public interface TrConsumer<T, S, U> {

    void accept(T t, S s, U u);

    default TrConsumer<T, S, U> andThen(TrConsumer<? super T, ? super S, ? super U> after) {
        Objects.requireNonNull(after);

        return (t, s, u) -> {
            accept(t, s, u);
            after.accept(t, s, u);
        };
    }

}
