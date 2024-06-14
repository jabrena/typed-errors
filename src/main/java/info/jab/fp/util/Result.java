package info.jab.fp.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A utility class representing a computation that may either result in a value (success)
 * or an exception (failure).
 *
 * @param <T> the type of the value
 *
 * @author Juan Antonio Breña Moral
 * @author ChatGTP-40
 */
public class Result<T> {

    private final T value;
    private final Exception exception;

    private Result(T value, Exception exception) {
        this.value = value;
        this.exception = exception;
    }

    /**
     * Creates a successful Result with the given value.
     *
     * @param value the value
     * @param <T> the type of the value
     * @return a successful Result
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    /**
     * Creates a failed Result with the given exception.
     *
     * @param exception the exception
     * @param <T> the type of the value
     * @return a failed Result
     */
    public static <T> Result<T> failure(Exception exception) {
        return new Result<>(null, exception);
    }

    /**
     * Checks if the Result is successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return exception == null;
    }

    /**
     * Checks if the Result is a failure.
     *
     * @return true if a failure, false otherwise
     */
    public boolean isFailure() {
        return exception != null;
    }

    /**
     * Performs the given action if the Result is successful.
     *
     * @param consumer the action to be performed
     */
    public void ifSuccess(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(value);
        }
    }

    /**
     * Performs the given action if the Result is a failure.
     *
     * @param consumer the action to be performed
     */
    public void ifFailure(Consumer<Exception> consumer) {
        if (isFailure()) {
            consumer.accept(exception);
        }
    }

    /**
     * Returns the value if present, otherwise returns an empty Optional.
     *
     * @return an Optional with the value or empty if failure
     */
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    /**
     * Returns the exception if present, otherwise returns an empty Optional.
     *
     * @return an Optional with the exception or empty if success
     */
    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    /**
     * Returns the value if the Result is successful, otherwise returns the result of the specified Supplier.
     * This method provides a way to lazily compute the default value if the Result is a failure.
     *
     * <pre>
     * {@code
     * Result<Integer> result = calculate("10", "2");
     * int finalValue = result.getOrElse(() -> {
     *     System.out.println("Error: " + result.getException().orElse(new Exception("Unknown error")).getMessage());
     *     return 0; // Default value in case of error
     * });
     * System.out.println("Final value: " + finalValue);
     * }
     * </pre>
     *
     * @param other a Supplier whose result is returned if the Result is a failure
     * @return the value if the Result is successful, otherwise the result of the Supplier
     */
    public T getOrElse(Supplier<? extends T> other) {
        return isSuccess() ? value : other.get();
    }

    /**
     * Applies the given function to the value if the Result is successful and returns a new Result.
     *
     * @param mapper the function to apply to the value
     * @param <U> the type of the value of the new Result
     * @return a new Result with the mapped value if successful, otherwise the original failure
     */
    public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        if (isSuccess()) {
            return Result.success(mapper.apply(value));
        } else {
            return Result.failure(exception);
        }
    }

    /**
     * Applies the given function to the value if the Result is successful and returns a new Result.
     * The function should return a new Result.
     *
     * @param mapper the function to apply to the value
     * @param <U> the type of the value of the new Result
     * @return the Result returned by the mapper if successful, otherwise the original failure
     */
    public <U> Result<U> flatMap(Function<? super T, Result<U>> mapper) {
        if (isSuccess()) {
            return mapper.apply(value);
        } else {
            return Result.failure(exception);
        }
    }

    /**
     * Applies the given function to the exception if the Result is a failure and returns a new Result
     * with the mapped value.
     *
     * @param mapper the function to apply to the exception
     * @return a new Result with the mapped value if failure, otherwise the original success
     */
    public Result<T> recover(Function<? super Exception, ? extends T> mapper) {
        if (isFailure()) {
            return Result.success(mapper.apply(exception));
        } else {
            return this;
        }
    }

    /**
     * Applies the given function to the exception if the Result is a failure and returns a new Result.
     * The function should return a new Result.
     *
     * @param mapper the function to apply to the exception
     * @return the Result returned by the mapper if failure, otherwise the original success
     */
    public Result<T> recoverCatching(Function<? super Exception, Result<T>> mapper) {
        if (isFailure()) {
            return mapper.apply(exception);
        } else {
            return this;
        }
    }

    /**
     * Executes the given supplier and returns a Result. If the supplier throws an exception,
     * it returns a failed Result with the thrown exception.
     *
     * @param supplier the supplier to execute
     * @param <T> the type of the value
     * @return a successful Result if the supplier succeeds, otherwise a failed Result
     */
    public static <T> Result<T> runCatching(CheckedSupplier<T> supplier) {
        try {
            return Result.success(supplier.get());
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    /**
     * A functional interface representing a supplier that may throw an exception.
     *
     * @param <T> the type of the value
     */
    @FunctionalInterface
    public interface CheckedSupplier<T> {
        /**
         * Gets a result, potentially throwing an exception.
         *
         * @return a result
         * @throws Exception if unable to supply a result
         */
        T get() throws Exception;
    }
}
