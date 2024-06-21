package info.jab.sc.policy;
/*
public static final class ShutdownOnSuccess<T> extends StructuredTaskScope<T> {
    private static final Object RESULT_NULL = new Object();
    private static final VarHandle FIRST_RESULT;
    private static final VarHandle FIRST_EXCEPTION;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            FIRST_RESULT = l.findVarHandle(ShutdownOnSuccess.class, "firstResult", Object.class);
            FIRST_EXCEPTION = l.findVarHandle(ShutdownOnSuccess.class, "firstException", Throwable.class);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    private volatile Object firstResult;
    private volatile Throwable firstException;

    public ShutdownOnSuccess(String name, ThreadFactory factory) {
        super(name, factory);
    }

    public ShutdownOnSuccess() {
        this(null, Thread.ofVirtual().factory());
    }

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        if (firstResult != null) {
            // already captured a result
            return;
        }

        if (subtask.state() == Subtask.State.SUCCESS) {
            // task succeeded
            T result = subtask.get();
            Object r = (result != null) ? result : RESULT_NULL;
            if (FIRST_RESULT.compareAndSet(this, null, r)) {
                super.shutdown();
            }
        } else if (firstException == null) {
            // capture the exception thrown by the first subtask that failed
            FIRST_EXCEPTION.compareAndSet(this, null, subtask.exception());
        }
    }

    @Override
    public ShutdownOnSuccess<T> join() throws InterruptedException {
        super.join();
        return this;
    }

    @Override
    public ShutdownOnSuccess<T> joinUntil(Instant deadline)
        throws InterruptedException, TimeoutException
    {
        super.joinUntil(deadline);
        return this;
    }

    public T result() throws ExecutionException {
        return result(ExecutionException::new);
    }

    public <X extends Throwable> T result(Function<Throwable, ? extends X> esf) throws X {
        Objects.requireNonNull(esf);
        ensureOwnerAndJoined();

        Object result = firstResult;
        if (result == RESULT_NULL) {
            return null;
        } else if (result != null) {
            @SuppressWarnings("unchecked")
            T r = (T) result;
            return r;
        }

        Throwable exception = firstException;
        if (exception != null) {
            X ex = esf.apply(exception);
            Objects.requireNonNull(ex, "esf returned null");
            throw ex;
        }

        throw new IllegalStateException("No completed subtasks");
    }
}
*/
