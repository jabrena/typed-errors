package info.jab.fp.util.raise;

/**
 * This class represents an {@link IllegalStateException} that is thrown
 * when a raise or bind operation is leaked outside of its context scope.
 */
class RaiseLeakedException extends IllegalStateException {

    /**
     * Constructs a new {@code RaiseLeakedException} with a default message indicating
     * that a raise or bind operation was leaked outside of its context scope.
     */
    public RaiseLeakedException() {
        super(
            "raise or bind was leaked outside of its context scope. Make sure all calls to raise and bind occur within the lifecycle of nullable { }, either { } or similar builders."
        );
    }
}
