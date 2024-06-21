package info.jab.sc.policy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class EnhancedTaskScope<T> extends StructuredTaskScope<T> {

    private Set<Subtask<? extends T>> criticalTasks = new HashSet<>();
    private int maxConsecutiveFails = -1;
    private AtomicInteger consecutiveFails = new AtomicInteger();
    private Throwable failedException;
    private T defaultValue;
    private Semaphore maxConcurrency;

    public EnhancedTaskScope() {}

    public EnhancedTaskScope(String name, ThreadFactory factory) {
        super(name, factory);
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public int getMaxConsecutiveFails() {
        return maxConsecutiveFails;
    }

    public void setMaxConsecutiveFails(int maxConsecutiveFails) {
        this.maxConsecutiveFails = maxConsecutiveFails;
    }

    /**
     * Define whether this scope should fail whenever a task fails, default is <code>false</code>.
     *
     * @param failOnException true to fail on first failing task, false otherwise.
     */
    public void setFailOnException(boolean failOnException) {
        maxConsecutiveFails = failOnException ? 0 : -1;
    }

    /**
     * When failOnException is set returns the first exception that failed this scope.
     *
     * @return the exception or <code>null</code> if getFailOnException is false or no exception occurred.
     */
    public Throwable getException() {
        return failedException;
    }

    /**
     * Set a maximum of concurrent tasks.
     * This method should be called before submitting the tasks
     *
     * @param maxConcurrentTasks max concurrent tasks, should be greater than 0.
     */
    public void setMaxConcurrentTasks(int maxConcurrentTasks) {
        if (maxConcurrentTasks <= 0) {
            throw new IllegalArgumentException("The number of maximum concurrent tasks should be greater than 0");
        }
        maxConcurrency = new Semaphore(maxConcurrentTasks);
    }

    /**
     * Sets a default value if the call fails. Note that <code>null</code> is not allowed as default value.
     * Also it's only used if <code>setFailOnException(true)</code> is not called.
     *
     * @param defaultValue the default value for fails tasks, cannot be <code>null</code>.
     */
    public void setDefaultValue(T defaultValue) {
        if (defaultValue == null) {
            throw new IllegalArgumentException("null is not allowed as default value.");
        }
        this.defaultValue = defaultValue;
    }

    @Override
    public <U extends T> StructuredTaskScope.Subtask<U> fork(Callable<? extends U> task) {
        if (maxConcurrency != null) {
            maxConcurrency.tryAcquire();
        }
        return super.fork(task);
    }

    public <U extends T> Subtask<U> forkCritical(Callable<? extends U> task) {
        Subtask<U> subtask = fork(task);
        criticalTasks.add(subtask);
        return subtask;
    }

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        if (maxConcurrency != null) {
            maxConcurrency.release();
        }
        super.handleComplete(subtask);
        if (
            subtask.state() == Subtask.State.FAILED && (consecutiveFails.incrementAndGet() > maxConsecutiveFails || criticalTasks.contains(subtask))
        ) {
            failedException = subtask.exception();
            shutdown();
        }
        if (subtask.state() == Subtask.State.SUCCESS && maxConsecutiveFails > 0) {
            consecutiveFails.set(0);
        }
    }

    public <U extends T> Subtask<U> forkWithDefault(Callable<? extends U> task, U defaultValue) {
        Callable<? extends U> newTask = () -> {
            try {
                return task.call();
            } catch (Exception ex) {
                return defaultValue;
            }
        };
        Subtask<U> subtask = fork(newTask);
        return subtask;
    }
}
