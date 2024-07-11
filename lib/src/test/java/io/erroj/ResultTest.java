package io.erroj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class ResultTest {

    @Test
    public void shouldInstanciateOk() {
        var res = Result.ok(10);

        assertTrue(res.isOk());
        assertEquals(10, res.okOrThrow());
    }

    @Test
    public void shouldInstanciateErr() {

        var toBeThrown = new NoSuchElementException();
        var res = Result.err(toBeThrown);

        assertTrue(res.isErr());
        assertEquals(toBeThrown, res.errOrThrow());
    }

    @Test
    public void shouldHandleExceptionWhenOfRunnable() {

        var toBeThrown = new ArithmeticException();
        boolean shouldThrow = true;

        var result = Result.of(() -> {
            if (shouldThrow)
                throw toBeThrown;
        });

        assertTrue(result.isErr());
        assertInstanceOf(VariableThrowable.class, result.errOrThrow());
        assertEquals(toBeThrown, result.errOrThrow().getWrapped());
    }

    @Test
    public void shouldHandleOkWhenOfRunnable() {

        var result = Result.of(() -> {
            return;
        });

        assertTrue(result.isOk());
        assertEquals(null, result.okOrThrow());
    }

    @Test
    public void shouldHandleExceptionWhenOfSupplier() {

        var toBeThrown = new ArithmeticException();
        boolean shouldThrow = true;

        var result = Result.of(() -> {
            if (shouldThrow)
                throw toBeThrown;
            return 10;
        });

        assertTrue(result.isErr());
        assertInstanceOf(VariableThrowable.class, result.errOrThrow());
        assertEquals(toBeThrown, result.errOrThrow().getWrapped());
    }

    @Test
    public void shouldHandleOkWhenOfSupplier() {

        var result = Result.of(() -> {
            return 10;
        });

        assertTrue(result.isOk());
        assertEquals(10, result.okOrThrow());
    }
}
