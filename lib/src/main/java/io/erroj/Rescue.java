package io.erroj;

import java.util.function.Supplier;

public sealed interface Rescue<V> permits SupplierRescue, ValueRescue {

    public Class<? extends Exception> matchedExceptionClass();

    public V rescuedValue();

    public static <E extends Exception, V> Rescue<V> of(Class<E> e, V val) {
        return new ValueRescue<>(e, val);
    }

    public static <E extends Exception, V> Rescue<V> of(Class<E> e, Supplier<V> consumer) {
        return new SupplierRescue<>(e, consumer);
    }
}
