package info.jab.fp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class EitherMonadLawsTest {

    @Test
    public void testMonad() {
        //map
        Either<String, Integer> left = Either.left("Test");
        Either<String, Integer> right = Either.right(42);
        //assertThat(left.map(x -> x + "1").get()).isEqualTo("Test1");
        assertThat(right.map(x -> x / 2).get()).isEqualTo(21);

        //ap
        Either<String, Function<Integer, Integer>> leftFn = Either.left("Nope");
        Either<String, Function<Integer, Integer>> rightFn = Either.right(x -> x / 2);
        //assertThat(leftFn.get()).isEqualTo("Nope"); //left biased like Haskell
        //assertThat(leftFn.get()).isEqualTo("Nope");
        //assertThat(eitherMonad.ap(rightFn, left).getLeft()).isEqualTo("Test");
        //assertThat(eitherMonad.ap(rightFn, right).getRight()).isEqualTo(21);

        //pure
        //assertThat(eitherMonad.pure(12).getRight()).isEqualTo(12);

        //bind
        Either<String, Integer> rightOdd = Either.right(43);
        Function<Integer, Either<String, Integer>> halfEven = x -> x % 2 == 0 ? Either.right(x / 2) : Either.left("Odd");
        //assertThat(eitherMonad.bind(left, halfEven).getLeft()).isEqualTo("Test");
        //assertThat(eitherMonad.bind(right, halfEven).getRight()).isEqualTo(21);
        //assertThat(eitherMonad.bind(rightOdd, halfEven).getLeft()).isEqualTo("Odd");
    }
}
