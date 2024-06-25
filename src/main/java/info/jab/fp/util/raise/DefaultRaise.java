package info.jab.fp.util.raise;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents a default implementation of the {@link Raise} interface.
 *
 * @param <E> the type of error that can be raised
 */
class DefaultRaise<E> implements Raise<E> {

    private final boolean isTraced;
    private final AtomicBoolean isActive = new AtomicBoolean(true);

    /**
     * Constructs a new {@code DefaultRaise} instance with the specified tracing option.
     *
     * @param isTraced {@code true} if the raise should be traced, {@code false} otherwise
     */
    public DefaultRaise(boolean isTraced) {
        this.isTraced = isTraced;
    }

    /**
     * Completes the raise operation, marking it as inactive.
     *
     * @return {@code true} if the raise was active before calling this method, {@code false} otherwise
     */
    public boolean complete() {
        return isActive.getAndSet(false);
    }

    /**
     * Raises an error of type {@code E}. If the raise is active, it throws either a {@code Traced}
     * or {@code NoTrace} exception based on the {@code isTraced} flag. If the raise is not active,
     * it throws a {@link RaiseLeakedException}.
     *
     * @param error the error to raise
     * @param <A> the return type
     * @return never returns normally
     * @throws Traced if {@code isTraced} is {@code true} and the raise is active
     * @throws NoTrace if {@code isTraced} is {@code false} and the raise is active
     * @throws RaiseLeakedException if the raise is not active
     */
    @Override
    public <A> A raise(E error) {
        if (isActive.get()) {
            throw isTraced ? new Traced(error, this, null) : new NoTrace(error, this);
        } else {
            throw new RaiseLeakedException();
        }
    }
}
