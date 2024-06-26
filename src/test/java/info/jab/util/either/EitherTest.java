package info.jab.util.either;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import info.jab.util.either.Either;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class EitherTest {

    @Test
    void testLeft() {
        Either<String, Integer> left = Either.left("Error");
        assertTrue(left.isLeft());
        assertFalse(left.isRight());
        assertEquals("Error", left.fold(Function.identity(), r -> "No Error"));
    }

    @Test
    void testRight() {
        Either<String, Integer> right = Either.right(42);
        assertFalse(right.isLeft());
        assertTrue(right.isRight());
        assertEquals("No Error", right.fold(l -> "Error", r -> "No Error"));
    }

    @Test
    void testGetLeft() {
        Either<String, Integer> left = Either.left("Test");
        Either<String, Integer> right = Either.right(42);
        assertThat(right.get()).isEqualTo(42);
        assertThatThrownBy(left::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testGetRight() {
        Either<String, Integer> left = Either.left("Test");
        Either<String, Integer> right = Either.right(42);
        assertThat(right.get()).isEqualTo(42);
        assertThatThrownBy(left::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testIsLeft() {
        Either<String, Integer> left = Either.left("Test");
        Either<String, Integer> right = Either.right(42);
        assertThat(left.isLeft()).isTrue();
        assertThat(right.isLeft()).isFalse();
    }

    @Test
    void testIsRight() {
        Either<String, Integer> left = Either.left("Test");
        Either<String, Integer> right = Either.right(42);
        assertThat(left.isRight()).isFalse();
        assertThat(right.isRight()).isTrue();
    }

    @Test
    void shouldEitherOrElseSupplier() {
        assertThat(Either.right(1).orElse(() -> Either.right(2)).get()).isEqualTo(1);
        assertThat(Either.left(1).orElse(() -> Either.right(2)).get()).isEqualTo(2);
    }

    @Test
    void shouldReturnTrueWhenCallingIsLeftOnLeft() {
        assertThat(Either.left(1).isLeft()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCallingIsRightOnLeft() {
        assertThat(Either.left(1).isRight()).isFalse();
    }

    @Test
    void shouldReturnTrueWhenCallingIsRightOnRight() {
        assertThat(Either.right(1).isRight()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCallingIsLeftOnRight() {
        assertThat(Either.right(1).isLeft()).isFalse();
    }

    @Test
    void testMap() {
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> mappedRight = right.map(x -> x + 1);
        assertTrue(mappedRight.isRight());
        assertEquals(43, mappedRight.fold(l -> -1, Function.identity()));

        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> mappedLeft = left.map(x -> x + 1);
        assertTrue(mappedLeft.isLeft());
        assertEquals("Error", mappedLeft.fold(Function.identity(), r -> "No Error"));
    }

    @Test
    void testLeftMap() {
        Either<String, Integer> left = Either.left("Test");
        Either<String, Integer> right = Either.right(42);
        //assertThat(left.map(x -> x + x).get()).isEqualTo("TestTest");
        assertThat(right.map(x -> x + x).get()).isEqualTo(84);
    }

    @Test
    void testRightMap() {
        Either<String, Integer> left = Either.left("Test");
        Either<String, Integer> right = Either.right(42);
        //assertThat(left.map(x -> x / 7).get()).isEqualTo("Test");
        assertThat(right.map(x -> x / 7).get()).isEqualTo(6);
    }

    private final Either<String, Integer> right = Either.right(1);
    private final Either<String, Integer> left = Either.left("A");

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
    void testFlatMap() {
        Either<String, Integer> right = Either.right(42);
        Either<String, Integer> flatMappedRight = right.flatMap(x -> Either.right(x + 1));
        assertTrue(flatMappedRight.isRight());
        assertEquals(43, flatMappedRight.fold(l -> -1, Function.identity()));

        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> flatMappedLeft = left.flatMap(x -> Either.right(x + 1));
        assertTrue(flatMappedLeft.isLeft());
        assertEquals("Error", flatMappedLeft.fold(Function.identity(), r -> "No Error"));
    }

    @Test
    void shouldFlatMapRight() {
        Either<String, Integer> either = Either.right(42);
        assertThat(either.flatMap(v -> Either.right("ok")).get()).isEqualTo("ok");
    }

    @Test
    void shouldFlatMapLeft() {
        Either<String, Integer> either = Either.left("vavr");
        assertThat(either.flatMap(v -> Either.right("ok"))).isEqualTo(either);
    }

    @Test
    void testSwap() {
        Either<String, Integer> right = Either.right(42);
        Either<Integer, String> swappedRight = right.swap();
        assertTrue(swappedRight.isLeft());
        assertEquals(42, swappedRight.fold(Function.identity(), r -> -1));

        Either<String, Integer> left = Either.left("Error");
        Either<Integer, String> swappedLeft = left.swap();
        assertTrue(swappedLeft.isRight());
        assertEquals("Error", swappedLeft.fold(l -> -1, Function.identity()));
    }

    @Test
    void shouldSwapLeft() {
        assertThat(Either.left(1).swap()).isEqualTo(Either.right(1));
    }

    @Test
    void shouldSwapRight() {
        assertThat(Either.right(1).swap()).isEqualTo(Either.left(1));
    }

    @Test
    void testGetOrElse() {
        Either<String, Integer> right = Either.right(42);
        assertEquals(42, right.getOrElse(() -> -1));

        Either<String, Integer> left = Either.left("Error");
        assertEquals(-1, left.getOrElse(() -> -1));
    }

    @Test
    void testOrElse() {
        Either<String, Integer> right = Either.right(42);
        assertEquals(42, right.orElse(() -> Either.right(-1)).fold(l -> -1, Function.identity()));

        Either<String, Integer> left = Either.left("Error");
        assertEquals(-1, left.orElse(() -> Either.right(-1)).fold(l -> -1, Function.identity()));
    }

    @Test
    void testCombine() {
        Either<String, Integer> right1 = Either.right(42);
        Either<String, Integer> right2 = Either.right(58);
        Either<String, Integer> combinedRight = right1.combine(right2, Integer::sum);
        assertTrue(combinedRight.isRight());
        assertEquals(100, combinedRight.fold(l -> -1, Function.identity()));

        Either<String, Integer> left = Either.left("Error");
        Either<String, Integer> combinedWithLeft = right1.combine(left, Integer::sum);
        assertTrue(combinedWithLeft.isRight());
        assertEquals(42, combinedWithLeft.fold(l -> -1, Function.identity()));

        Either<String, Integer> combinedBothLeft = left.combine(left, Integer::sum);
        assertTrue(combinedBothLeft.isLeft());
        assertEquals("Error", combinedBothLeft.fold(Function.identity(), r -> "No Error"));
    }

    @Test
    void shouldFoldLeft() {
        final String value = Either.left("L").fold(l -> l + "+", r -> r + "-");
        assertThat(value).isEqualTo("L+");
    }

    @Test
    void shouldFoldRight() {
        final String value = Either.right("R").fold(l -> l + "-", r -> r + "+");
        assertThat(value).isEqualTo("R+");
    }

    @Test
    void shouldEqualLeftIfObjectIsSame() {
        final Either<Integer, ?> left = Either.left(1);
        assertThat(left.equals(left)).isTrue();
    }

    @Test
    void shouldNotEqualLeftIfObjectIsNull() {
        assertThat(Either.left(1).equals(null)).isFalse();
    }

    @Test
    void shouldNotEqualLeftIfObjectIsOfDifferentType() {
        assertThat(Either.left(1).equals(new Object())).isFalse();
    }

    @Test
    void shouldEqualLeft() {
        assertThat(Either.left(1)).isEqualTo(Either.left(1));
    }

    @Test
    void shouldEqualRightIfObjectIsSame() {
        final Either<?, ?> right = Either.right(1);
        assertThat(right.equals(right)).isTrue();
    }

    @Test
    void shouldNotEqualRightIfObjectIsNull() {
        assertThat(Either.right(1).equals(null)).isFalse();
    }

    @Test
    void shouldNotEqualRightIfObjectIsOfDifferentType() {
        assertThat(Either.right(1).equals(new Object())).isFalse();
    }

    @Test
    void testToOptionalRight() {
        Either<String, Integer> either = Either.right(42);
        Optional<Integer> optional = either.toOptional();

        assertTrue(optional.isPresent());
        assertEquals(42, optional.get());
    }

    @Test
    void testToOptionalLeft() {
        Either<String, Integer> either = Either.left("Error");
        Optional<Integer> optional = either.toOptional();

        assertFalse(optional.isPresent());
    }

    @Test
    void testLeftValueCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            Either.left(null);
        });

        assertEquals("Left value cannot be null", exception.getMessage());
    }

    @Test
    void testRightValueCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            Either.right(null);
        });

        assertEquals("Right value cannot be null", exception.getMessage());
    }

    @Test
    void shouldEqualRight() {
        assertThat(Either.right(1)).isEqualTo(Either.right(1));
    }

    @Test
    void testToString() {
        assertThat(Either.left("Test").toString()).isEqualTo("Left[value=Test]");
        assertThat(Either.right(42).toString()).isEqualTo("Right[value=42]");
    }
}
