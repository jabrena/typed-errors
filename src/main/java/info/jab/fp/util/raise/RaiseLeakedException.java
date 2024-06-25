package info.jab.fp.util.raise;

class RaiseLeakedException extends IllegalStateException {

    public RaiseLeakedException() {
        super(
            "raise or bind was leaked outside of its context scope. Make sure all calls to raise and bind occur within the lifecycle of nullable { }, either { } or similar builders."
        );
    }
}
