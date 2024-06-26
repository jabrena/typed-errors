package info.jab.util.result;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Record representing a failed Result in the Result.
 *
 * <p>This record encapsulates the failure state of a computation within the Result,
 * holding an exception that caused the failure.
 *
 * @param <T> the type of the value expected in a successful computation
 * @param exception the exception that caused the failure
 */
public record Failure<T>(Throwable exception) implements Result<T> {
    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public void ifSuccess(Consumer<T> consumer) {
        // No action needed for failure
    }

    @Override
    public void ifFailure(Consumer<Throwable> consumer) {
        consumer.accept(exception);
    }

    @Override
    public Optional<T> getValue() {
        return Optional.empty();
    }

    @Override
    public Optional<Throwable> getException() {
        return Optional.ofNullable(exception);
    }

    @Override
    public T getOrElse(Supplier<? extends T> other) {
        return other.get();
    }

    @Override
    public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        return new Failure<>(exception);
    }

    @Override
    public <U> Result<U> flatMap(Function<? super T, Result<U>> mapper) {
        return new Failure<>(exception);
    }

    @Override
    public Result<T> recover(Function<? super Throwable, ? extends T> mapper) {
        return new Success<>(mapper.apply(exception));
    }

    @Override
    public Result<T> recoverCatching(Function<? super Throwable, Result<T>> mapper) {
        return mapper.apply(exception);
    }

    @Override
    public <U> U fold(U initialValue, Function<? super T, U> folder) {
        return initialValue; // Use the initial value for failures
    }
}
