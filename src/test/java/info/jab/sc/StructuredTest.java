package info.jab.sc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

public class StructuredTest {

    @Test
    public void testStructured_throwsException() throws Exception {
        Supplier<Object> supplier = () -> {
            throw new RuntimeException("test exception");
        };

        assertThrows(RuntimeException.class, () -> Structured.structured(supplier));
    }

    @Test
    public void testJoinAll_handlesCancelledFiber() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Object> cancelledCallable = () -> {
            Thread.sleep(100); // Simulate some work
            return null;
        };

        Future<Object> future = executor.submit(cancelledCallable);
        future.cancel(true);

        Structured structured = new Structured("test", Thread.ofVirtual().factory());
        structured.externalFibers.set(Collections.singletonList(future));

        structured.joinAll(structured);

        // No verification needed for cancelled fiber
        executor.shutdown();
    }

    @Test
    public void testJoinAll_joinsScope() throws Exception {
        Structured structured = new Structured("test", Thread.ofVirtual().factory());
        structured.externalFibers.set(Collections.emptyList());

        structured.joinAll(structured);

        structured.scope.join(); // Manual join for testing
    }

    @Test
    public void testTrack() {
        Structured structured = new Structured("test", Thread.ofVirtual().factory());
        Future<Object> future = new CompletableFuture<>();

        structured.track(future);

        assertTrue(structured.externalFibers.get().contains(future));
    }
}
