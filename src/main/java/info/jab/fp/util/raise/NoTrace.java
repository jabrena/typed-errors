package info.jab.fp.util.raise;

/**
 * This class represents a non-traced {@link RaiseCancellationException}, which does not have nested causes.
 */
public final class NoTrace extends RaiseCancellationException {

    /**
     * Constructs a new {@code NoTrace} exception with the specified raised object and raise instance.
     *
     * @param raised the object that was raised during the cancellation
     * @param raise the {@code Raise} instance associated with this exception
     */
    public NoTrace(Object raised, Raise<?> raise) {
        super(raised, raise);
    }
}
