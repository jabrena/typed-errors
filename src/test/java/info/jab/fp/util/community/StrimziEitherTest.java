package info.jab.fp.util.community;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import info.jab.fp.util.Either;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

public class StrimziEitherTest {

    private final Either<String, Integer> right = Either.right(1);
    private final Either<String, Integer> left = Either.left("A");

    @Test
    void testAccessors() {
        assertTrue(right.isRight());
        assertFalse(left.isRight());
        assertEquals(1, right.get());
        //assertThrows(IllegalStateException.class, right::left);
        //assertEquals("A", left.get());
        assertThrows(NoSuchElementException.class, left::get);
        //assertThrows(IllegalStateException.class, left::right);
    }

    @Test
    void testEquals() {
        assertEquals(Either.<String, Integer>right(1), right);
        assertNotEquals(Either.<String, Integer>right(2), right);
        assertEquals(Either.<String, Integer>right(1).hashCode(), right.hashCode());
        assertNotEquals(Either.<String, Integer>right(2).hashCode(), right.hashCode());
        //assertTrue(right.isRightEqual(1));
        //assertFalse(right.isRightEqual(2));

        assertEquals(Either.<String, Integer>left("A"), left);
        assertNotEquals(Either.<String, Integer>left("B"), left);
        assertEquals(Either.<String, Integer>left("A").hashCode(), left.hashCode());
        assertNotEquals(Either.<String, Integer>left("B").hashCode(), left.hashCode());
        //assertFalse(left.isRightEqual(1));
        //assertFalse(left.isRightEqual(2));
    }

    @Test
    void testMapLeft() {
        var mappedRight = right.map(i -> i + 1);
        var mappedLeft = left.map(i -> i + 1);
        assertEquals(Either.<String, Integer>right(2), mappedRight);
        assertEquals(left, mappedLeft);
    }

    @Test
    void testFlatMapLeft() {
        var mappedRight = right.flatMap(i -> Either.right(i + 1));
        var mappedLeft = left.flatMap(i -> Either.left("B"));
        assertEquals(Either.right(2), mappedRight);
        assertEquals(left, mappedLeft);
    }

    @Test
    void testToString() {
        assertEquals("Right[value=1]", right.toString());
        assertEquals("Left[value=A]", left.toString());
    }
}
