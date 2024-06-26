package info.jab.fp.util.raise;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultRaiseTest {

    private static final String ERROR_MESSAGE = "Test error";

    private DefaultRaise<String> raiseWithTrace;
    private DefaultRaise<String> raiseWithoutTrace;

    @BeforeEach
    void setUp() {
        raiseWithTrace = new DefaultRaise<>(true);
        raiseWithoutTrace = new DefaultRaise<>(false);
    }

    @Test
    void testRaiseAfterComplete() {
        raiseWithTrace.complete();
        assertThrows(RaiseLeakedException.class, () -> {
            raiseWithTrace.raise(ERROR_MESSAGE);
        });
    }

    @Test
    void testComplete() {
        assertTrue(raiseWithTrace.complete());
        assertFalse(raiseWithTrace.complete());
    }

    @Test
    void testRaiseWithoutTraceAfterComplete() {
        raiseWithoutTrace.complete();
        assertThrows(RaiseLeakedException.class, () -> {
            raiseWithoutTrace.raise(ERROR_MESSAGE);
        });
    }

    @Test
    void testIsActive() {
        assertThrows(Traced.class, () -> raiseWithTrace.raise(ERROR_MESSAGE));
        assertThrows(NoTrace.class, () -> raiseWithoutTrace.raise(ERROR_MESSAGE));
        raiseWithTrace.complete();
        raiseWithoutTrace.complete();
        assertThrows(RaiseLeakedException.class, () -> raiseWithTrace.raise(ERROR_MESSAGE));
        assertThrows(RaiseLeakedException.class, () -> raiseWithoutTrace.raise(ERROR_MESSAGE));
    }
}
