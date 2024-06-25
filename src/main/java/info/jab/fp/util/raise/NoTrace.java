package info.jab.fp.util.raise;

final class NoTrace extends RaiseCancellationException {

    public NoTrace(Object raised, Raise<?> raise) {
        super(raised, raise);
    }
}
