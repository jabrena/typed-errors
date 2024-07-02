package info.jab.sc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

class CustomScope<T> extends StructuredTaskScope<T> {

    //TODO Review if ConcurrentLinkedQueue is the right data structure
    private final Queue<T> results = new ConcurrentLinkedQueue<>();

    CustomScope() {
        super(null, Thread.ofVirtual().factory());
    }

    @Override
    protected void handleComplete(StructuredTaskScope.Subtask<? extends T> subtask) {
        if (subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
            T result = subtask.get();
            results.add(result);
        }
    }

    public Stream<T> stream() {
        return results.stream();
    }
}
