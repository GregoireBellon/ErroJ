package io.erroj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ErrTest {
    @Test
    public void souldExtractErr() {
        var except = new NoSuchElementException();
        Result<Integer, Exception> result = new Err<Integer, Exception>(except);

        switch (result) {
            case Err(var v) -> assertEquals(except, v);
            default -> fail();
        }
    }

    @Test
    public void souldBeErrWhenErr() {
        Result<Integer, Exception> result = new Err<Integer, Exception>(new NoSuchElementException());
        assertTrue(result.isErr());
    }

    @Test
    public void souldNotBeOkWhenErr() {
        Result<Integer, Exception> result = new Err<Integer, Exception>(new NoSuchElementException());
        assertFalse(result.isOk());
    }

    @Test
    public void souldNotTriggerIfOkConsumer() {
        Result<Integer, Exception> result = new Err<Integer, Exception>(new NoSuchElementException());
        result.ifOk((i) -> fail("This lambda should not be triggered"));
    }

    @Test
    public void souldNotTriggerIfOkRunnable() {
        Result<Integer, Exception> result = new Err<Integer, Exception>(new NoSuchElementException());
        result.ifOk(() -> fail("This lambda should not be triggered"));
    }

    @Test
    public void souldTriggerIfErrConsumer() {
        var except = new NoSuchElementException();
        final boolean wasVisited[] = { false };

        Result<Integer, Exception> result = new Err<Integer, Exception>(except);
        result.ifErr((e) -> {
            assertEquals(e, except);
            wasVisited[0] = true;
        });

        assertTrue(wasVisited[0]);
    }

    @Test
    public void souldTriggerIfErrRunnable() {
        final boolean wasVisited[] = { false };

        Result<Integer, Exception> result = new Err<Integer, Exception>(new NoSuchElementException());
        result.ifErr(() -> {
            wasVisited[0] = true;
        });

        assertTrue(wasVisited[0]);
    }

    @Test
    public void souldNotGetOptionalOkWhenErr() {
        Result<Integer, Exception> result = new Err<Integer, Exception>(new NoSuchElementException());
        Optional<Integer> maybeOk = result.ok();

        assertTrue(maybeOk.isEmpty());
    }

    @Test
    public void souldGetOptionalErrWhenErr() {
        var err = new NoSuchElementException();

        Result<Integer, NoSuchElementException> result = new Err<>(err);

        Optional<NoSuchElementException> maybeErr = result.err();

        assertTrue(maybeErr.isPresent());
        assertEquals(err, maybeErr.orElseThrow());
    }

    @Test
    public void shouldRecoverWhenError() {
        String errMsg = "test error";
        Result<String, NoSuchElementException> result = new Err<>(new NoSuchElementException(errMsg));

        var res = result.recover(r -> r.getMessage());

        assertEquals(errMsg, res);
    }

    @Test
    public void shouldFlatRecover() {
        String errMsg = "test error";
        Result<String, NoSuchElementException> result = new Err<>(new NoSuchElementException(errMsg));

        var res = result.flatRecover(r -> new Ok<>(r.getMessage()));

        assertEquals(errMsg, res.okOrThrow());
    }

    @Test
    public void shouldFlatRecoverWithSuperclassAndReturnSubclass() {
        Object errMsg = "test error";
        Result<Object, NoSuchElementException> result = new Err<>(new NoSuchElementException((String) errMsg));

        var res = result.flatRecover((Exception r) -> new Ok<>(r.getMessage()));

        assertEquals(errMsg, res.okOrThrow());
    }

    @Test
    public void shouldFlatRecoverWithExcepion() {
        String errMsg = "test error";
        String arithErrMsg = "arithmetic error";
        Result<String, NoSuchElementException> result = new Err<>(new NoSuchElementException(errMsg));

        var res = result.flatRecover(r -> new Err<>(new ArithmeticException(arithErrMsg)));

        assertTrue(res.isErr());
        assertInstanceOf(ArithmeticException.class, res.errOrThrow());
        assertEquals(arithErrMsg, res.errOrThrow().getMessage());
    }

    @Test
    public void shouldTriggerOrElseGet() {

        Result<Integer, NoSuchElementException> result = new Err<>(new NoSuchElementException());

        var elseVal = result.orElseGet(() -> 100);

        assertEquals(100, elseVal);
        ;
    }

    @Test
    public void shouldGetElseValue() {

        Result<Integer, NoSuchElementException> result = new Err<>(new NoSuchElementException());

        var elseVal = result.orElse(100);

        assertEquals(100, elseVal);
        ;
    }

    @Test
    public void shouldThrowWithMessageWhenOkOrThrow() {
        String failMessage = "failMessage";
        String throwMessage = "";

        Result<Integer, NoSuchElementException> result = new Err<>(new NoSuchElementException());

        try {
            result.okOrThrow(failMessage);
        } catch (NoSuchElementException e) {
            throwMessage = e.getMessage();
        }

        assertEquals(failMessage, throwMessage);
    }

    @Test
    public void shouldThrowWhenOkOrThrow() {
        Result<Integer, ArithmeticException> result = new Err<>(new ArithmeticException());

        assertThrows(NoSuchElementException.class, result::okOrThrow);
    }

    @Test
    public void shouldReThrow() {
        Result<Integer, ArithmeticException> result = new Err<>(new ArithmeticException());

        assertThrows(ArithmeticException.class, result::orReThrow);
    }

    @Test
    public void shouldMapErr() {
        String failMessage = "failMessage";
        Result<Integer, NoSuchElementException> result = new Err<>(new NoSuchElementException(failMessage));

        var mappedResult = result.mapErr(err -> new ArithmeticException(err.getMessage()));

        assertTrue(mappedResult.isErr());
        assertInstanceOf(ArithmeticException.class, mappedResult.errOrThrow());
        assertEquals(failMessage, mappedResult.errOrThrow().getMessage());
    }

    @Test
    public void shouldGetErrWhenErrOrThrow() {

        Result<Integer, ArithmeticException> result = new Err<>(new ArithmeticException());

        var except = result.errOrThrow();

        assertInstanceOf(ArithmeticException.class, except);
    }

    @Test
    public void shouldGetErrWhenErrOrThrowWithMessage() {

        Result<Integer, ArithmeticException> result = new Err<>(new ArithmeticException());

        var except = result.errOrThrow("useless message");

        assertInstanceOf(ArithmeticException.class, except);
    }

    @Test
    public void shouldNotMap() {

        var except = new ArithmeticException();
        Result<Integer, ArithmeticException> result = new Err<>(except);

        result.map((i) -> {
            fail("This function should not be invoked");
            return i * 2;
        });

        assertEquals(except, result.errOrThrow());
    }

    @Test
    public void shouldReturnItselfWrappedInVariableThrowableWhenFlatMap() {

        var except = new ArithmeticException();

        Result<Integer, ArithmeticException> result = new Err<>(except);

        var res = result.flatMap((i) -> {
            fail("This function should not be invoked");
            return new Ok<>(Integer.toString(i * 2));
        });

        assertTrue(res.isErr());
        assertEquals(except, res.errOrThrow().getWrapped());
    }

    @Test
    public void givenVariableThrowableShouldReturnItselfWhenFlatMap() {

        var except = new VariableThrowable(new ArithmeticException());

        Result<Integer, VariableThrowable> result = new Err<>(except);

        var res = result.flatMap((i) -> {
            fail("This lambda should not be invoked");
            return new Ok<>(Integer.toString(i * 2));
        });

        assertTrue(res.isErr());
        assertEquals(except, res.errOrThrow());
    }

    @Test
    public void givenVariableThrowableShouldReturnItselfWhenAndThen() {

        var except = new VariableThrowable(new ArithmeticException());

        Result<Integer, VariableThrowable> result = new Err<>(except);

        Result<String, VariableThrowable> res = result.andThen((Integer i) -> {
            fail("This lambda should not be invoked");
            return Integer.toString(i * 2);
        });

        assertTrue(res.isErr());
        assertEquals(except, res.errOrThrow());
    }

    @Test
    public void shouldReturnItselfWrappedWhenAndThen() {

        var except = new ArithmeticException();

        Result<Integer, ArithmeticException> result = new Err<>(except);

        Result<String, VariableThrowable> res = result.andThen((Integer i) -> {
            fail("This lambda should not be invoked");
            return Integer.toString(i * 2);
        });

        assertTrue(res.isErr());
        assertInstanceOf(VariableThrowable.class, res.errOrThrow());
        assertEquals(except, res.errOrThrow().getWrapped());
    }

    @Test
    public void givenVariableThrowableShouldReturnItselfWhenAndThenConsumer() {

        var except = new VariableThrowable(new ArithmeticException());

        Result<Integer, VariableThrowable> result = new Err<>(except);

        Result<Integer, VariableThrowable> res = result.andThen((Integer i) -> {
            fail("This lambda should not be invoked");
        });

        assertTrue(res.isErr());
        assertEquals(except, res.errOrThrow());
    }

    @Test
    public void shouldReturnItselfWrappedWhenAndThenConsumer() {

        var except = new ArithmeticException();

        Result<Integer, ArithmeticException> result = new Err<>(except);

        Result<Integer, VariableThrowable> res = result.andThen((Integer i) -> {
            fail("This lambda should not be invoked");
        });

        assertTrue(res.isErr());
        assertInstanceOf(VariableThrowable.class, res.errOrThrow());
        assertEquals(except, res.errOrThrow().getWrapped());
    }

    @Test
    public void shouldRescueWhenMatchedException() {

        var result = new Err<>(new NoSuchElementException());
        var res = result.rescue(Rescue.of(NoSuchElementException.class, () -> 10));

        assertTrue(res.isOk());
        assertEquals(10, res.okOrThrow());
    }

    @Test
    public void shouldRescueWithValueWhenMatchedException() {
        var result = new Err<>(new NoSuchElementException());

        var res = result.rescue(Rescue.of(NoSuchElementException.class, 10));

        assertTrue(res.isOk());
        assertEquals(10, res.okOrThrow());
    }

    @Test
    public void shouldNotRescueWhenUnMatchedException() {

        var result = new Err<>(new NoSuchElementException());

        var res = result.rescue(Rescue.of(ArithmeticException.class, () -> 10));

        assertTrue(res.isErr());
    }

}
