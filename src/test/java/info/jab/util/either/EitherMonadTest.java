package info.jab.util.either;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.jab.util.either.Either;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class EitherMonadTest {

    @Test
    void testLeftIdentity() {
        Integer value = 5;
        Function<Integer, Either<String, Integer>> f = x -> Either.right(x + 3);
        Either<String, Integer> either = Either.right(value);

        assertEquals(f.apply(value), either.flatMap(f));
    }

    @Test
    void testRightIdentity() {
        Either<String, Integer> right = Either.right(5);

        assertEquals(right, right.flatMap(Either::right));
    }

    @Test
    void testAssociativity() {
        Either<String, Integer> right = Either.right(5);
        Function<Integer, Either<String, Integer>> f = x -> Either.right(x + 2);
        Function<Integer, Either<String, Integer>> g = x -> Either.right(x * 3);

        Either<String, Integer> leftSide = right.flatMap(f).flatMap(g);
        Either<String, Integer> rightSide = right.flatMap(x -> f.apply(x).flatMap(g));

        assertEquals(leftSide, rightSide);
    }
}
