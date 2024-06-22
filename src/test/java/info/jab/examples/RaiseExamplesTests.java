package info.jab.examples;

import info.jab.fp.util.Either;
import java.util.Objects;
import org.junit.jupiter.api.Test;

public class RaiseExamplesTests {

    @Test
    void raiseDSL() {
        Either<String, Integer> op = Either.right(1);

        var result = Either.either(raise -> {
            var a = raise.bind(op);
            var b = raise.bind(op);
            var c = raise.bind(op);
            return a + b + c;
        });

        assert Objects.equals(result, Either.right(3));
    }

    @Test
    void raiseDSLLeft() {
        Either<String, Integer> op = Either.left("error");

        var result = Either.either(raise -> {
            var a = raise.bind(op);
            var b = raise.bind(op);
            var c = raise.bind(op);
            return a + b + c;
        });

        assert Objects.equals(result, Either.left("error"));
    }
}
