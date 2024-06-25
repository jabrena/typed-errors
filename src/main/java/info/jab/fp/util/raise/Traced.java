package info.jab.fp.util.raise;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

final class Traced extends RaiseCancellationException {

    private final Traced cause;

    public Traced(@Nonnull Object raised, @Nonnull Raise<?> raise, @Nullable Traced cause) {
        super(raised, raise);
        this.cause = cause;
    }

    public Traced getCause() {
        return cause;
    }
}
