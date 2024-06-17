package info.jab.fp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void testSuccess() {
        Result<String> result = Result.success("Success");

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals(Optional.of("Success"), result.getValue());
        assertEquals(Optional.empty(), result.getException());
    }

    @Test
    void testFailure() {
        Exception exception = new Exception("Failure");
        Result<String> result = Result.failure(exception);

        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertEquals(Optional.empty(), result.getValue());
        assertEquals(Optional.of(exception), result.getException());
    }

    @Test
    void testIfSuccess() {
        Result<String> result = Result.success("Success");
        result.ifSuccess(value -> assertEquals("Success", value));
        result.ifFailure(exception -> fail("Should not be called"));
    }

    @Test
    void testIfFailure() {
        Exception exception = new Exception("Failure");
        Result<String> result = Result.failure(exception);
        result.ifSuccess(value -> fail("Should not be called"));
        result.ifFailure(e -> assertEquals(exception, e));
    }

    @Test
    void testGetOrElse() {
        Result<String> successResult = Result.success("Success");
        Result<String> failureResult = Result.failure(new Exception("Failure"));

        assertEquals("Success", successResult.getOrElse(() -> "Default"));
        assertEquals("Default", failureResult.getOrElse(() -> "Default"));
    }

    @Test
    void testMap() {
        Result<Integer> successResult = Result.success(5);
        Result<Integer> mappedSuccessResult = successResult.map(value -> value * 2);

        assertTrue(mappedSuccessResult.isSuccess());
        assertEquals(Optional.of(10), mappedSuccessResult.getValue());

        Result<Integer> failureResult = Result.failure(new Exception("Failure"));
        Result<Integer> mappedFailureResult = failureResult.map(value -> value * 2);

        assertTrue(mappedFailureResult.isFailure());
        assertEquals(Optional.empty(), mappedFailureResult.getValue());
    }

    @Test
    void testFlatMap() {
        Result<Integer> successResult = Result.success(5);
        Result<Integer> flatMappedSuccessResult = successResult.flatMap(value -> Result.success(value * 2));

        assertTrue(flatMappedSuccessResult.isSuccess());
        assertEquals(Optional.of(10), flatMappedSuccessResult.getValue());

        Result<Integer> failureResult = Result.failure(new Exception("Failure"));
        Result<Integer> flatMappedFailureResult = failureResult.flatMap(value -> Result.success(value * 2));

        assertTrue(flatMappedFailureResult.isFailure());
        assertEquals(Optional.empty(), flatMappedFailureResult.getValue());
    }

    @Test
    void testRecover() {
        Result<Integer> successResult = Result.success(5);
        Result<Integer> recoveredSuccessResult = successResult.recover(exception -> 10);

        assertTrue(recoveredSuccessResult.isSuccess());
        assertEquals(Optional.of(5), recoveredSuccessResult.getValue());

        Result<Integer> failureResult = Result.failure(new Exception("Failure"));
        Result<Integer> recoveredFailureResult = failureResult.recover(exception -> 10);

        assertTrue(recoveredFailureResult.isSuccess());
        assertEquals(Optional.of(10), recoveredFailureResult.getValue());
    }

    @Test
    void testRecoverCatching() {
        Result<Integer> successResult = Result.success(5);
        Result<Integer> recoveredSuccessResult = successResult.recoverCatching(exception -> Result.success(10));

        assertTrue(recoveredSuccessResult.isSuccess());
        assertEquals(Optional.of(5), recoveredSuccessResult.getValue());

        Result<Integer> failureResult = Result.failure(new Exception("Failure"));
        Result<Integer> recoveredFailureResult = failureResult.recoverCatching(exception -> Result.success(10));

        assertTrue(recoveredFailureResult.isSuccess());
        assertEquals(Optional.of(10), recoveredFailureResult.getValue());
    }

    @Test
    void testrunCatching() {
        Result<Integer> successResult = Result.runCatching(() -> 5);
        assertTrue(successResult.isSuccess());
        assertEquals(Optional.of(5), successResult.getValue());

        Result<Integer> failureResult = Result.runCatching(() -> {
            throw new Exception("Failure");
        });
        assertTrue(failureResult.isFailure());
        assertEquals("Failure", failureResult.getException().get().getMessage());
    }

    @Test
    void testFoldSuccessWithFunction() {
        Result<String> successResult = Result.success("Hello");
        String foldedValue = successResult.fold("", value -> "Greetings, " + value);
        assertEquals("Greetings, Hello", foldedValue);
    }

    @Test
    void testFoldFailureWithInitialValue() {
        Result<String> failedResult = Result.failure(new Exception("Error"));
        String foldedValue = failedResult.fold("World", value -> "Greetings, " + value);
        assertEquals("World", foldedValue);
    }

    @Test
    void testFoldGenericType() {
        Result<Integer> successResult = Result.success(42);
        String foldedValue = successResult.fold("", value -> String.valueOf(value * 2));
        assertEquals("84", foldedValue);
    }

    interface Doubler<T extends Number> {
        T doubleValue(T value);
    }

    @Test
    void testFoldCustomFunction() {
        Result<Double> successResult = Result.success(3.14);
        Doubler<Double> doubler = value -> value * 2;
        String foldedValue = successResult.fold("", value -> String.valueOf(doubler.doubleValue(value)));
        assertEquals("6.28", foldedValue);
    }
}
