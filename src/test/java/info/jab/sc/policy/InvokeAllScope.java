package info.jab.sc.policy;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class InvokeAllScope<T> extends StructuredTaskScope<T> {

    private final Queue<T> results = new ConcurrentLinkedQueue<>();
    private final AtomicInteger successCounter = new AtomicInteger(0);
    private final AtomicInteger failureCounter = new AtomicInteger(0);
    private final AtomicInteger unavailableCounter = new AtomicInteger(0);

    public InvokeAllScope() {
        super(null, Thread.ofVirtual().factory());
    }

    @Override
    protected void handleComplete(StructuredTaskScope.Subtask<? extends T> subtask) {
        if (subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
            T result = subtask.get();
            results.add(result);
            successCounter.getAndIncrement();
        } else if (subtask.state() == StructuredTaskScope.Subtask.State.FAILED) {
            failureCounter.getAndIncrement();
        } else if (subtask.state() == StructuredTaskScope.Subtask.State.UNAVAILABLE) {
            unavailableCounter.getAndIncrement();
        }
    }

    private void emitCounters() {
        boolean doEmit = true;
        if (doEmit) {
            System.out.println("TRACER scope # success: " + successCounter.get());
            System.out.println("TRACER scope # failure: " + failureCounter.get());
            System.out.println("TRACER scope # unavailable : " + unavailableCounter.get());
        }
    }

    public Stream<T> forkAll(List<Callable<T>> tasks) throws Exception {
        for (var task : tasks) {
            this.fork(() -> task.call());
        }

        this.join();
        emitCounters();

        return this.results.stream();
    }
}
