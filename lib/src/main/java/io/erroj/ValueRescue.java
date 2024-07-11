package io.erroj;

public record ValueRescue<V>(Class<? extends Exception> matchedExceptionClass, V rescuedValue)
        implements Rescue<V> {

}
