package io.erroj;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Consumer;

public final class VariableThrowable extends Throwable {

    private final Throwable thrown;

    public VariableThrowable(Throwable throwable) {
        super(throwable);
        this.thrown = throwable;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public synchronized Throwable getCause() {
        return this.thrown.getCause();
    }

    @Override
    public String getLocalizedMessage() {
        return this.thrown.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return this.thrown.getMessage();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return this.thrown.getStackTrace();
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return this.thrown.initCause(cause);
    }

    @Override
    public void printStackTrace() {
        this.thrown.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        this.thrown.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        this.thrown.printStackTrace(s);
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.thrown.setStackTrace(stackTrace);
    }

    @Override
    public String toString() {
        return this.thrown.toString();
    }

    public Throwable getWrapped() {
        return this.thrown;
    }

    @SuppressWarnings("unchecked")
    public <E extends Throwable> VariableThrowable doOn(Class<E> exceptClazz, Consumer<? super E> consumer) {
        if (exceptClazz.isInstance(thrown)) {
            // safe, runtime check before cast
            consumer.accept((E) this.thrown);
        }
        return this;
    }

    public <E extends Throwable> VariableThrowable doOn(Class<E> exceptClazz, Runnable runnable) {
        if (exceptClazz.isInstance(thrown)) {
            runnable.run();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <E extends Throwable> Optional<E> getThrowable(Class<E> exceptClazz) {

        if (exceptClazz.isInstance(thrown)) {
            // safe, runtime check before cast
            return Optional.of((E) this.thrown);
        }
        return Optional.empty();
    }

    @SafeVarargs
    final public <V> Result<V, VariableThrowable> rescue(
            Rescue<V>... rescuers) {

        for (var resc : rescuers) {
            if (resc.matchedExceptionClass().isInstance(thrown)) {
                return new Ok<>(resc.rescuedValue());
            }
        }

        return new Err<>(this);
    }

}
