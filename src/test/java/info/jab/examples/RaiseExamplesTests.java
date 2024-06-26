package info.jab.examples;

import static org.assertj.core.api.Assertions.assertThat;

import info.jab.fp.util.either.Either;
import org.junit.jupiter.api.Test;

public class RaiseExamplesTests {

    @Test
    void raiseDSL() {
        //Given
        Either<String, Integer> op = Either.right(1);

        //When
        var result = Either.either(raise -> {
            var a = raise.bind(op);
            var b = raise.bind(op);
            var c = raise.bind(op);
            return a + b + c;
        });

        //Then
        var expectedResult = Either.right(3);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void raiseDSLLeft() {
        //Given
        Either<String, Integer> op = Either.left("error");

        //When
        var result = Either.either(raise -> {
            var a = raise.bind(op);
            var b = raise.bind(op);
            var c = raise.bind(op);
            return a + b + c;
        });

        //Then
        var expectedResult = Either.left("error");
        assertThat(result).isEqualTo(expectedResult);
    }
}
