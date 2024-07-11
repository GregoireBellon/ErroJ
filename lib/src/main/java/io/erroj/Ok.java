package io.erroj;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record Ok<V, E extends Throwable>(V value) implements Result<V, E> {

    @Override
    public boolean isErr() {
        return false;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public void ifOk(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void ifOk(Consumer<? super V> consumer) {
        consumer.accept(value);
    }

    @Override
    public void ifErr(Consumer<? super E> consumer) {
        return;
    }

    @Override
    public void ifErr(Runnable runnable) {
        return;
    }

    @Override
    public Optional<V> ok() {
        return Optional.of(value);
    }

    @Override
    public Optional<E> err() {
        return Optional.empty();
    }

    @Override
    public V recover(Function<E, V> recoverer) {
        return value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <OE extends Throwable> Result<V, OE> flatRecover(
            ResultMapper<? super E, V, OE> recoverer) {
        // safe, since we cast an hypothetic Err value that doesn't exists
        return (Result<V, OE>) this;
    }

    @Override
    public V orElseGet(Supplier<? extends V> supplier) {
        return value;
    }

    @Override
    public V orElse(V other) {
        return value;
    }

    @Override
    public V okOrThrow(String failMessage) throws NoSuchElementException {
        return value;
    }

    @Override
    public V okOrThrow() throws NoSuchElementException {
        return value;
    }

    @Override
    public V orReThrow() throws E {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Throwable> Result<V, T> mapErr(Function<? super E, ? extends T> mapper) {
        // safe, since we casts an hypothetic Err value that doesn't exists
        return (Result<V, T>) this;
    }

    @Override
    public E errOrThrow() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public E errOrThrow(String err) throws NoSuchElementException {
        throw new NoSuchElementException(err);
    }

    @Override
    public <T> Result<T, E> map(Function<V, ? extends T> mapper) {
        return new Ok<T, E>(mapper.apply(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <OV, OE extends Throwable> Result<OV, VariableThrowable> flatMap(
            ResultMapper<? super V, OV, OE> mapper) {

        // the ? extends OV type is lost when we invoke mapErr
        // mapErr is certified to not alter the Ok value, so the cast is safe
        return (Result<OV, VariableThrowable>) mapper
                .apply(value)
                .<VariableThrowable>mapErr(VariableThrowable::new);
    }

    @Override
    public <OV, OE extends Throwable> Result<OV, VariableThrowable> andThen(
            CheckedFunction<? super V, ? extends OV> mapper) {
        try {
            return new Ok<>(mapper.apply(value));
        } catch (Throwable t) {
            return new Err<>(new VariableThrowable(t));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OE extends Throwable> Result<V, VariableThrowable> andThen(CheckedConsumer<? super V> consumer) {
        try {
            consumer.accept(value);
            // safe, since we cast an hypothetic Err value that doesn't exists
            return (Result<V, VariableThrowable>) this;
        } catch (Throwable t) {
            return new Err<>(new VariableThrowable(t));
        }
    }

    @SafeVarargs
    @Override
    public final Result<V, E> rescue(Rescue<V>... rescues) {
        return this;
    }

}
