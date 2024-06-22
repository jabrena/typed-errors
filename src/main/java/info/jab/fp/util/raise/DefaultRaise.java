package info.jab.fp.util.raise;

import java.util.concurrent.atomic.AtomicBoolean;

class DefaultRaise<E> implements Raise<E> {

    private final boolean isTraced;
    private final AtomicBoolean isActive = new AtomicBoolean(true);

    public DefaultRaise(boolean isTraced) {
        this.isTraced = isTraced;
    }

    public boolean complete() {
        return isActive.getAndSet(false);
    }

    @Override
    public <A> A raise(E error) {
        if (isActive.get()) {
            throw isTraced ? new Traced(error, this, null) : new NoTrace(error, this);
        } else {
            throw new RaiseLeakedException();
        }
    }
}
