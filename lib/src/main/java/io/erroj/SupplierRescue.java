package io.erroj;

import java.util.function.Supplier;

public record SupplierRescue<V>(Class<? extends Exception> matchedExceptionClass, Supplier<V> rescuer)
        implements Rescue<V> {

    @Override
    public V rescuedValue() {
        return rescuer().get();
    }
}
