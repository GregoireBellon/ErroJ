package io.erroj;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class OkTest {

    @Test
    public void souldExtractOk() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        switch (result) {
            case Ok(var v) -> assertEquals(10, v);
            default -> fail();
        }
    }

    @Test
    public void souldNotBeErrWhenOk() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);
        assertFalse(result.isErr());
    }

    @Test
    public void shouldExecIfOkConsumer() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        // array because otherwise wasVisited is not final
        final boolean[] wasVisited = { false };

        result.ifOk((val) -> {
            assertEquals(10, val);
            wasVisited[0] = true;
        });

        assertTrue(wasVisited[0]);
    }

    @Test
    public void shouldExecIfOkRunnable() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        // array because otherwise wasVisited is not final
        final boolean[] wasVisited = { false };

        result.ifOk(() -> {
            wasVisited[0] = true;
        });

        assertTrue(wasVisited[0]);
    }

    @Test
    public void shouldNotExecIfErrConsumer() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        result.ifErr((val) -> {
            fail("This lambda should not be triggered");
        });
    }

    @Test
    public void shouldNotExecIfErrRunnable() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        result.ifErr(() -> {
            fail("This lambda should not be triggered");
        });
    }

    @Test
    public void souldGetOptionalOkWhenOk() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        Optional<Integer> maybeOk = result.ok();

        assertTrue(maybeOk.isPresent());
        assertEquals(10, maybeOk.orElseThrow());
    }

    @Test
    public void souldNotGetOptionalErrWhenOk() {
        Result<Integer, NoSuchElementException> result = new Ok<>(10);

        Optional<NoSuchElementException> maybeErr = result.err();

        assertTrue(maybeErr.isEmpty());
    }

    @Test
    public void shouldReturnValueWhenRecover() {
        Result<Integer, NoSuchElementException> result = new Ok<>(10);

        assertEquals(10, result.recover(e -> 20));
    }

    @Test
    public void shouldReturnItselfWhenFlatRecover() {
        Result<Integer, NoSuchElementException> result = new Ok<>(10);

        var recoverRes = result.flatRecover(e -> new Ok<>(20));

        assertTrue(recoverRes.isOk());
        assertEquals(10, recoverRes.okOrThrow());
    }

    @Test
    public void shouldNotExecOrElseGetWhenOk() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        // array because otherwise wasVisited is not final
        final boolean[] wasVisited = { false };

        var res = result.orElseGet(() -> {
            wasVisited[0] = true;
            return 0;
        });

        assertFalse(wasVisited[0]);
        assertEquals(10, res);
    }

    @Test
    public void shouldNotOrElseWhenOk() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        var res = result.orElse(0);

        assertEquals(10, res);
    }

    @Test
    public void shouldGetTryOk() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        var res = result.okOrThrow();

        assertEquals(10, res);
    }

    @Test
    public void shouldGetTryOkMsg() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        var res = result.okOrThrow("Should not throw");

        assertEquals(10, res);
    }

    @Test
    public void shouldNotRethrow() throws Exception {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        assertDoesNotThrow(result::orReThrow);
        assertEquals(10, result.orReThrow());
    }

    @Test
    public void shouldMapErrorButStillBeOk() throws Exception {
        Result<Integer, NoSuchElementException> result = new Ok<Integer, NoSuchElementException>(10);

        assertDoesNotThrow(() -> {

            Result<Integer, NoSuchFieldException> mapped = result
                    .mapErr(err -> new NoSuchFieldException(err.getMessage()));

            assertTrue(mapped.isOk());
        });
    }

    @Test
    public void shouldThrowWhenTryErr() {

        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        assertThrows(NoSuchElementException.class, result::errOrThrow);
    }

    @Test
    public void shouldThrowWhenTryErrMessage() {

        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        assertThrows(NoSuchElementException.class, () -> result.errOrThrow("Should throw"));
    }

    @Test
    public void shouldMap() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        Result<String, Exception> mapped = result.map(i -> i.toString());

        assertEquals("10", mapped.okOrThrow());
    }

    @Test
    public void shouldReturnOkWhenFlatMapOk() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        var andThenRes = result.flatMap(r -> new Ok<>(r * 2));

        assertTrue(andThenRes.isOk());
        assertEquals(20, andThenRes.okOrThrow());
    }

    @Test
    public void shouldReturnErrWhenFlatMapErr() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        var andThenRes = result.flatMap(r -> new Err<>(new NoSuchElementException()));

        assertTrue(andThenRes.isErr());
        assertInstanceOf(VariableThrowable.class, andThenRes.errOrThrow());
        assertInstanceOf(NoSuchElementException.class, andThenRes.errOrThrow().getWrapped());
    }

    @Test
    public void shouldReturnOkWhenAndThenAndOk() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        Result<String, VariableThrowable> res = result.andThen((Integer i) -> {
            return Integer.toString(i);
        });

        assertTrue(res.isOk());
        assertEquals("10", res.okOrThrow());
    }

    @Test
    public void shouldReturnErrWhenAndThenAndErr() {

        var thrownExcept = new ArithmeticException();

        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        Result<String, VariableThrowable> res = result.andThen((Integer i) -> {
            if (i == 10)
                throw thrownExcept;
            return Integer.toString(i);
        });

        assertTrue(res.isErr());
        assertInstanceOf(VariableThrowable.class, res.errOrThrow());
        assertEquals(thrownExcept, res.errOrThrow().getWrapped());
    }

    @Test
    public void shouldReturnItselfWhenAndThenAndOkConsumer() {
        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        Result<Integer, VariableThrowable> res = result.andThen(i -> {
        });

        assertTrue(res.isOk());
        assertEquals(10, res.okOrThrow());
    }

    @Test
    public void shouldReturnErrWhenAndThenAndErrConsumer() {

        var thrownExcept = new ArithmeticException();

        Result<Integer, Exception> result = new Ok<Integer, Exception>(10);

        Result<Integer, VariableThrowable> res = result.andThen((Integer i) -> {
            if (i == 10)
                throw thrownExcept;
        });

        assertTrue(res.isErr());
        assertInstanceOf(VariableThrowable.class, res.errOrThrow());
        assertEquals(thrownExcept, res.errOrThrow().getWrapped());

    }

    @Test
    public void shouldReturnItselfWhenRescue() {

        var result = new Ok<>(10);
        var res = result.rescue(Rescue.of(NoSuchElementException.class, () -> 20));

        assertTrue(res.isOk());
        assertEquals(result, res);
    }

}
