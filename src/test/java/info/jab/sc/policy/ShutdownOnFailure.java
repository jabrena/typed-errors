package info.jab.sc.policy;
/*
public static final class ShutdownOnFailure extends StructuredTaskScope<Object> {
    private static final VarHandle FIRST_EXCEPTION;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            FIRST_EXCEPTION = l.findVarHandle(ShutdownOnFailure.class, "firstException", Throwable.class);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    private volatile Throwable firstException;

    public ShutdownOnFailure(String name, ThreadFactory factory) {
        super(name, factory);
    }

    public ShutdownOnFailure() {
        this(null, Thread.ofVirtual().factory());
    }

    @Override
    protected void handleComplete(Subtask<?> subtask) {
        if (subtask.state() == Subtask.State.FAILED
                && firstException == null
                && FIRST_EXCEPTION.compareAndSet(this, null, subtask.exception())) {
            super.shutdown();
        }
    }

    @Override
    public ShutdownOnFailure join() throws InterruptedException {
        super.join();
        return this;
    }

    @Override
    public ShutdownOnFailure joinUntil(Instant deadline)
        throws InterruptedException, TimeoutException
    {
        super.joinUntil(deadline);
        return this;
    }

    public Optional<Throwable> exception() {
        ensureOwnerAndJoined();
        return Optional.ofNullable(firstException);
    }

    public void throwIfFailed() throws ExecutionException {
        throwIfFailed(ExecutionException::new);
    }

    public <X extends Throwable>
    void throwIfFailed(Function<Throwable, ? extends X> esf) throws X {
        ensureOwnerAndJoined();
        Objects.requireNonNull(esf);
        Throwable exception = firstException;
        if (exception != null) {
            X ex = esf.apply(exception);
            Objects.requireNonNull(ex, "esf returned null");
            throw ex;
        }
    }
}
*/
