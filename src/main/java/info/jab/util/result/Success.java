package info.jab.util.result;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Record representing a successful Result in the Result.
 *
 * <p>This record encapsulates the successful state of a computation within the Result,
 * holding the value resulting from a successful computation.
 *
 * @param <T> the type of the value returned in a successful computation
 * @param value the value resulting from a successful computation
 */
public record Success<T>(T value) implements Result<T> {
    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public void ifSuccess(Consumer<T> consumer) {
        consumer.accept(value);
    }

    @Override
    public void ifFailure(Consumer<Throwable> consumer) {
        // No action needed for success
    }

    @Override
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    @Override
    public Optional<Throwable> getException() {
        return Optional.empty();
    }

    @Override
    public T getOrElse(Supplier<? extends T> other) {
        return value;
    }

    @Override
    public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        return new Success<>(mapper.apply(value));
    }

    @Override
    public <U> Result<U> flatMap(Function<? super T, Result<U>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public Result<T> recover(Function<? super Throwable, ? extends T> mapper) {
        return this; // No recovery needed for success
    }

    @Override
    public Result<T> recoverCatching(Function<? super Throwable, Result<T>> mapper) {
        return this; // No recovery needed for success
    }

    @Override
    public <U> U fold(U initialValue, Function<? super T, U> folder) {
        return folder.apply(value); // Apply folder to the successful value
    }
}
