package io.erroj;

@FunctionalInterface
interface ResultMapper<T, V, E extends Throwable> {
    Result<? extends V, ? extends E> apply(T v);
}
