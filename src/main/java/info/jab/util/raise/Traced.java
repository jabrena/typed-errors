package info.jab.util.raise;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class represents a traced {@link RaiseCancellationException}, allowing for nested causes.
 */
public final class Traced extends RaiseCancellationException {

    /**
     * Traced
     */
    private final Traced cause;

    /**
     * Constructs a new {@code Traced} exception with the specified raised object, raise instance, and cause.
     *
     * @param raised the object that was raised during the cancellation
     * @param raise the {@code Raise} instance associated with this exception
     * @param cause the cause of this exception, or {@code null} if there is no cause
     */
    public Traced(@Nonnull Object raised, @Nonnull Raise<?> raise, @Nullable Traced cause) {
        super(raised, raise);
        this.cause = cause;
    }

    /**
     * Returns the cause of this exception, or {@code null} if there is no cause.
     *
     * @return the cause of this exception, or {@code null}
     */
    @Nullable
    @Override
    public synchronized Traced getCause() {
        return cause;
    }
}
