package info.jab.fp.util.either;

import jakarta.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

/**
 * A record representing the Right variant of an Either.
 *
 * @param <L> the type of the Left value
 * @param <R> the type of the Right value
 * @param value the value
 */
public record Right<L, R>(@Nonnull R value) implements Either<L, R> {
    /**
     * Constructs a new {@code Right} with the given value.
     *
     * @param value the value to be contained in this {@code Right}
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public Right {
        Objects.requireNonNull(value, "Right value cannot be null");
    }

    @Override
    public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
        return rightMapper.apply(value);
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @Override
    public R get() {
        return value;
    }
}
