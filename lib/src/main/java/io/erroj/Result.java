package io.erroj;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import java.util.function.Function;

public sealed interface Result<V, E extends Throwable> permits Err, Ok {

    boolean isErr();

    boolean isOk();

    void ifOk(Consumer<? super V> consumer);

    void ifOk(Runnable runnable);

    void ifErr(Consumer<? super E> consumer);

    void ifErr(Runnable runnable);

    Optional<V> ok();

    Optional<E> err();

    V recover(Function<E, V> recoverer);

    public <OE extends Throwable> Result<V, OE> flatRecover(
            ResultMapper<? super E, V, OE> recoverer);

    V orElseGet(Supplier<? extends V> supplier);

    V orElse(V other);

    V okOrThrow(String failMessage) throws NoSuchElementException;

    V okOrThrow() throws NoSuchElementException;

    V orReThrow() throws E;

    E errOrThrow() throws NoSuchElementException;

    E errOrThrow(String err) throws NoSuchElementException;

    <T extends Throwable> Result<V, T> mapErr(Function<? super E, ? extends T> mapper);

    <T> Result<T, E> map(Function<V, ? extends T> mapper);

    <OV, OE extends Throwable> Result<OV, VariableThrowable> flatMap(
            ResultMapper<? super V, OV, OE> mapper);

    <OV, OE extends Throwable> Result<OV, VariableThrowable> andThen(
            CheckedFunction<? super V, ? extends OV> mapper);

    <OE extends Throwable> Result<V, VariableThrowable> andThen(
            CheckedConsumer<? super V> consumer);

    @SuppressWarnings("unchecked")
    Result<V, E> rescue(Rescue<V>... rescues);

    public static Result<Void, VariableThrowable> of(CheckedRunnable callable) {
        try {
            callable.run();
            return new Ok<>(null);
        } catch (Throwable e) {
            return new Err<>(new VariableThrowable(e));
        }
    }

    public static <V> Result<V, VariableThrowable> of(CheckedSupplier<? extends V> supplier) {
        try {
            return new Ok<V, VariableThrowable>(supplier.get());
        } catch (Throwable e) {
            return new Err<V, VariableThrowable>(new VariableThrowable(e));
        }
    }

    public static <V, E extends Throwable> Ok<V, E> ok(V val) {
        return new Ok<>(val);
    }

    public static <V, E extends Throwable> Err<V, E> err(E thrown) {
        return new Err<>(thrown);
    }
}
