package info.jab.util.raise;

import jakarta.annotation.Nonnull;
import java.util.concurrent.CancellationException;

/**
 * This class represents a specific type of {@link CancellationException} that occurs
 * when a raise operation is canceled.
 *
 * <p>It is a sealed class that permits only {@code Traced} and {@code NoTrace} subclasses.</p>
 */
public sealed class RaiseCancellationException extends CancellationException permits Traced, NoTrace {

    /**
     * A constant message used to indicate that a {@code RaiseCancellationException} has been captured.
     */
    public static final String RaiseCancellationExceptionCaptured = "RaiseCancellationExceptionCaptured";

    /**
     * Raised
     */
    private final transient Object raised;

    /**
     * Raise
     */
    private final transient Raise<?> raise;

    /**
     * Constructs a new {@code RaiseCancellationException} with the specified raised object and raise instance.
     *
     * @param raised the object that was raised during the cancellation
     * @param raise the {@code Raise} instance associated with this exception
     */
    public RaiseCancellationException(@Nonnull Object raised, @Nonnull Raise<?> raise) {
        super(RaiseCancellationExceptionCaptured);
        this.raised = raised;
        this.raise = raise;
    }

    /**
     * Returns the object that was raised during the cancellation.
     *
     * @return the raised object
     */
    public Object getRaised() {
        return raised;
    }

    /**
     * Returns the {@code Raise} instance associated with this exception.
     *
     * @return the {@code Raise} instance
     */
    public Raise<?> getRaise() {
        return raise;
    }
}
