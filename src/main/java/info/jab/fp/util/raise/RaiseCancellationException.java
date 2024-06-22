package info.jab.fp.util.raise;

import jakarta.annotation.Nonnull;
import java.util.concurrent.CancellationException;

public sealed class RaiseCancellationException extends CancellationException permits Traced, NoTrace {

    public static final String RaiseCancellationExceptionCaptured = "RaiseCancellationExceptionCaptured";

    private final Object raised;
    private final Raise<?> raise;

    public RaiseCancellationException(@Nonnull Object raised, @Nonnull Raise<?> raise) {
        super(RaiseCancellationExceptionCaptured);
        this.raised = raised;
        this.raise = raise;
    }

    public Object getRaised() {
        return raised;
    }

    public Raise<?> getRaise() {
        return raise;
    }
}
