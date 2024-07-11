package io.erroj;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record Err<V, E extends Throwable>(E thrown) implements Result<V, E> {

    @Override
    public boolean isErr() {
        return true;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public void ifOk(Consumer<? super V> consumer) {
        return;
    }

    @Override
    public void ifOk(Runnable runnable) {
        return;
    }

    @Override
    public void ifErr(Consumer<? super E> consumer) {
        consumer.accept(thrown);
    }

    @Override
    public void ifErr(Runnable runnable) {
        runnable.run();
    }

    @Override
    public Optional<V> ok() {
        return Optional.empty();
    }

    @Override
    public Optional<E> err() {
        return Optional.of(thrown);
    }

    @Override
    public V recover(Function<E, V> recoverer) {
        return recoverer.apply(thrown);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OE extends Throwable> Result<V, OE> flatRecover(
            ResultMapper<? super E, V, OE> recoverer) {
        // safe, since we return either a subclass of V, or OE into V, or OE
        return (Result<V, OE>) recoverer.apply(thrown);
    }

    @Override
    public V orElse(V other) {
        return other;
    }

    @Override
    public V okOrThrow(String failMessage) throws NoSuchElementException {
        throw new NoSuchElementException(failMessage);
    }

    @Override
    public V okOrThrow() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public V orReThrow() throws E {
        throw thrown;
    }

    @Override
    public E errOrThrow() throws NoSuchElementException {
        return thrown;
    }

    @Override
    public E errOrThrow(String err) throws NoSuchElementException {
        return thrown;
    }

    @Override
    public V orElseGet(Supplier<? extends V> supplier) {
        return supplier.get();
    }

    @Override
    public <T extends Throwable> Result<V, T> mapErr(Function<? super E, ? extends T> mapper) {
        return new Err<V, T>(mapper.apply(thrown));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Result<T, E> map(Function<V, ? extends T> mapper) {
        // safe, since we cast an hypothetic Ok value that doesn't exists
        return (Result<T, E>) this;
    }

    @Override
    public <OV, OE extends Throwable> Result<OV, VariableThrowable> flatMap(
            ResultMapper<? super V, OV, OE> mapper) {
        return itselfCastedToVariableThrowable();
    }

    @Override
    public <OV, OE extends Throwable> Result<OV, VariableThrowable> andThen(
            CheckedFunction<? super V, ? extends OV> mapper) {
        return itselfCastedToVariableThrowable();
    }

    @Override
    public <OE extends Throwable> Result<V, VariableThrowable> andThen(
            CheckedConsumer<? super V> consumer) {
        return itselfCastedToVariableThrowable();
    }

    @SuppressWarnings("unchecked")
    private <OV, OE extends Throwable> Result<OV, VariableThrowable> itselfCastedToVariableThrowable() {
        if (thrown instanceof VariableThrowable) {
            // safe, since we check if E is already a VariableThrowable
            return (Result<OV, VariableThrowable>) this;
        }
        return (Result<OV, VariableThrowable>) this.mapErr(VariableThrowable::new);

    }

    @Override
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final Result<V, E> rescue(Rescue<V>... rescues) {

        if (thrown instanceof VariableThrowable vt) {
            // safe, since we check if E is already a VariableThrowable
            // which is what vt.rescue returns
            return (Result<V, E>) vt.rescue(rescues);
        }

        for (var resc : rescues) {
            if (resc.matchedExceptionClass().isInstance(thrown)) {
                return new Ok<>(resc.rescuedValue());
            }
        }

        return this;
    }
}
