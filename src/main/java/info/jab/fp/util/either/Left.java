package info.jab.fp.util.either;

import jakarta.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * A record representing the Left variant of an Either.
 *
 * @param <L> the type of the Left value
 * @param <R> the type of the Right value
 * @param value the value
 */
public record Left<L, R>(@Nonnull L value) implements Either<L, R> {
    /**
     * Constructs a new {@code Left} with the given value.
     *
     * @param value the value to be contained in this {@code Left}
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public Left {
        Objects.requireNonNull(value, "Left value cannot be null");
    }

    @Override
    public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
        return leftMapper.apply(value);
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @Override
    public R get() {
        throw new NoSuchElementException("No value present in Left");
    }
}
