package io.erroj;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}