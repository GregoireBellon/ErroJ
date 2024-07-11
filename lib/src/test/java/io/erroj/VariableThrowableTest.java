package io.erroj;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.NoSuchElementException;

public class VariableThrowableTest {

    @Test
    public void shouldInstanciate() {
        new VariableThrowable(new NoSuchElementException());
    }

    @Test
    public void shouldDoOnWhenRightThrowable() {
        VariableThrowable e = new VariableThrowable(new NoSuchElementException());

        // array because otherwise wasVisited is not final
        final boolean[] wasVisited = { false };

        e.doOn(NoSuchElementException.class, nse -> {
            assertInstanceOf(NoSuchElementException.class, nse);
            wasVisited[0] = true;
        });

        assertTrue(wasVisited[0]);
    }

    @Test
    public void shouldDoOnWhenAssignableThrowable() {
        VariableThrowable e = new VariableThrowable(new NoSuchElementException());

        // array because otherwise wasVisited is not final
        final boolean[] wasVisited = { false };

        e.doOn(RuntimeException.class, nse -> {
            assertInstanceOf(NoSuchElementException.class, nse);
            wasVisited[0] = true;
        });

        assertTrue(wasVisited[0]);
    }

    @Test
    public void shouldNotDoOnWhenWrongThrowable() {
        VariableThrowable e = new VariableThrowable(new NoSuchElementException());

        e.doOn(IllegalAccessException.class, nse -> {
            fail("This lambda should not be invoked");
        });
    }

    @Test
    public void shouldDoOnRunnableWhenAssignableThrowable() {
        VariableThrowable e = new VariableThrowable(new NoSuchElementException());

        // array because otherwise wasVisited is not final
        final boolean[] wasVisited = { false };

        e.doOn(RuntimeException.class, () -> {
            wasVisited[0] = true;
        });

        assertTrue(wasVisited[0]);
    }

    @Test
    public void shouldNotDoOnWRunnablehenWrongThrowable() {
        VariableThrowable e = new VariableThrowable(new NoSuchElementException());

        e.doOn(IllegalAccessException.class, () -> {
            fail("This lambda should not be invoked");
        });
    }

    @Test
    public void shouldGetThrowableWhenValidClass() {

        var exception = new NoSuchElementException();
        VariableThrowable vt = new VariableThrowable(exception);

        var optThrowable = vt.getThrowable(NoSuchElementException.class);

        assertTrue(optThrowable.isPresent());
        assertEquals(exception, optThrowable.orElseThrow());
    }

    @Test
    public void shouldNotGetThrowableWhenInvalidClass() {

        var exception = new NoSuchElementException();
        VariableThrowable vt = new VariableThrowable(exception);

        var optThrowable = vt.getThrowable(ArithmeticException.class);

        assertTrue(optThrowable.isEmpty());
    }

    @Test
    public void shouldGetCause() {
        VariableThrowable vt = new VariableThrowable(new NoSuchElementException());
        assertEquals(vt.getWrapped().getCause(), vt.getCause());
    }

    @Test
    public void shouldGetLocalizedMessage() {
        VariableThrowable vt = new VariableThrowable(new NoSuchElementException());
        assertEquals(vt.getWrapped().getLocalizedMessage(), vt.getLocalizedMessage());
    }

    @Test
    public void shouldGetMessage() {
        VariableThrowable vt = new VariableThrowable(new NoSuchElementException());
        assertEquals(vt.getWrapped().getMessage(), vt.getMessage());
    }

    @Test
    public void shouldGetStackTrace() {
        VariableThrowable vt = new VariableThrowable(new NoSuchElementException());
        assertArrayEquals(vt.getWrapped().getStackTrace(), vt.getStackTrace());
    }

    @Test
    public void shouldGetString() {
        VariableThrowable vt = new VariableThrowable(new NoSuchElementException());
        assertEquals(vt.getWrapped().toString(), vt.toString());
    }

    @Test
    public void shouldRescueWhenMatchedException() {
        VariableThrowable vt = new VariableThrowable(new NoSuchElementException());

        var result = new Err<>(vt);
        var res = result.rescue(Rescue.of(NoSuchElementException.class, () -> 10));

        assertTrue(res.isOk());
        assertEquals(10, res.okOrThrow());
    }

    @Test
    public void shouldRescueWithValueWhenMatchedException() {
        VariableThrowable vt = new VariableThrowable(new NoSuchElementException());
        var result = new Err<>(vt);

        var res = result.rescue(Rescue.of(NoSuchElementException.class, 10));

        assertTrue(res.isOk());
        assertEquals(10, res.okOrThrow());
    }

    @Test
    public void shouldNotRescueWhenUnMatchedException() {
        VariableThrowable vt = new VariableThrowable(new NoSuchElementException());

        var result = new Err<>(vt);

        var res = result.rescue(Rescue.of(ArithmeticException.class, () -> 10));

        assertTrue(res.isErr());
    }

}
