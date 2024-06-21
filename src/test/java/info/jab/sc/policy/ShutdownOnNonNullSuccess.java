package info.jab.sc.policy;

import java.util.concurrent.StructuredTaskScope;

public class ShutdownOnNonNullSuccess<T> extends StructuredTaskScope<T> {

    private volatile T rresult;

    public ShutdownOnNonNullSuccess() {
        super(null, Thread.ofVirtual().factory());
    }

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        var state = subtask.state();
        if (state == StructuredTaskScope.Subtask.State.UNAVAILABLE) {
            throw new IllegalArgumentException("Task is not completed");
        } else if (state == StructuredTaskScope.Subtask.State.SUCCESS) {
            // Get the result of the Subtask.
            T result = subtask.get();

            // The first non-null result is stored and the scope is
            // shutdown.
            if (result != null) {
                rresult = result;
                shutdown();
            }
        }
    }

    @Override
    public ShutdownOnNonNullSuccess<T> join() throws InterruptedException {
        super.join();
        return this;
    }

    public T result() {
        return rresult;
    }
}
