package info.jab.sc;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Stream;

// Annotations are not directly translatable but comments can be used
// to explain the purpose
// @implicitNotFound annotation can be replaced with a comment
// explaining the requirement for StructuredTaskScope
public class Structured {

    private final String name;
    private final ThreadFactory threadFactory;
    final AtomicReference<List<Future<Object>>> externalFibers;
    final StructuredTaskScope<Object> scope;

    public Structured(String name, ThreadFactory threadFactory) {
        this.name = name;
        this.threadFactory = threadFactory != null ? threadFactory : Executors.defaultThreadFactory();
        this.externalFibers = new AtomicReference<>(Collections.emptyList());
        this.scope = new StructuredTaskScope<>();
    }

    // Static method to create a Structured instance (similar to inline def)
    public static Structured structured(Supplier<Object> f) {
        String name = "structured";
        ThreadFactory threadFactory = Thread.ofVirtual().factory();
        StructuredTaskScope<Object> scope = new StructuredTaskScope<>();
        Structured structured = new Structured(name, threadFactory);
        try {
            return (Structured) f.get();
        } finally {
            joinAll(structured);
            scope.close();
        }
    }

    static void joinAll(Structured structured) {
        structured.externalFibers
            .get()
            .forEach(fiber -> {
                if (!fiber.isCancelled()) {
                    try {
                        fiber.get(); // Use get() instead of join() for consistency
                    } catch (CancellationException e) {
                        // Handle cancellation exception (optional)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        // Handle execution exception (optional)
                    }
                }
            });

        //TODO: Review
        try {
            structured.scope.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void track(Future<Object> fiber) {
        externalFibers.updateAndGet(fibers -> Collections.unmodifiableList(Stream.concat(fibers.stream(), Stream.of(fiber)).toList()));
    }

    Callable<Object> callableOf(Supplier<Object> f) {
        return () -> f.get();
    }
}
