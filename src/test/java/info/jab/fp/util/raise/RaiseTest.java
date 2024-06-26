package info.jab.fp.util.raise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import info.jab.fp.util.Either;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RaiseTest {

    private static final Logger logger = LoggerFactory.getLogger(RaiseTest.class);

    @Test
    void should_either_with_raise_ok() {
        //Given
        Either<String, Integer> op = Either.right(42);

        //When
        var result = Either.either(raise -> raise.bind(op));

        //Then
        assertThat(result.get()).isEqualTo(42);
    }

    enum ConnectionProblem {
        INVALID_URI,
    }

    Function<String, Either<ConnectionProblem, URI>> toUri = param -> {
        try {
            return Either.right(new URI(param));
        } catch (URISyntaxException ex) {
            logger.warn(ex.getMessage(), ex);
            return Either.left(ConnectionProblem.INVALID_URI);
        }
    };

    @Test
    void should_raise_work_with_multiple_either_right() {
        //Given
        var operation1 = toUri.apply("http://www.google.com");
        var operation2 = toUri.apply("http://www.nvidia.com");

        //When
        var result = Either.either(raise -> {
            var a = raise.bind(operation1);
            var b = raise.bind(operation2);
            return (Objects.nonNull(a) && Objects.nonNull(b));
        });

        //Then
        var expectedResult = Either.right(true);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void should_raise_work_with_either_right_and_left() {
        //Given
        Either<ConnectionProblem, URI> operation1 = toUri.apply("http://www.google.com");
        Either<ConnectionProblem, URI> operation2 = toUri.apply("%http://www.nvidia.com");

        //When
        var result = Either.either(raise -> {
            var a = raise.bind(operation1);
            var b = raise.bind(operation2);
            return (Objects.nonNull(a) && Objects.nonNull(b));
        });

        //Then
        var expectedResult = Either.left(ConnectionProblem.INVALID_URI);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testBindWithRight() {
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
    void testBindWithLeft() {
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

    @Test
    void testRaise() {
        //Given
        String error = "RaiseCancellationExceptionCaptured";

        //When
        var raise = new DefaultRaise<>(false);

        // @formatter:off

        // Then
        assertThatThrownBy(() -> raise.raise(error))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(error);
        // @formatter:on
    }

    // @formatter:off

    @Test
    void testFoldWithNoError() {
        String result = Raise.fold(
            raise -> "Success", 
            throwable -> "Caught Exception", 
            error -> "Recovered", 
            resultValue -> resultValue + "!");

        assertEquals("Success!", result);
    }

    // @formatter:on

    @Test
    void testFoldWithRaiseError() {
        String result = Raise.fold(
            raise -> {
                raise.raise("Error");
                return "Won't reach here";
            },
            throwable -> "Caught Exception",
            error -> "Recovered: " + error,
            resultValue -> resultValue + "!"
        );

        assertEquals("Recovered: Error", result);
    }

    @Test
    void testFoldWithException() {
        String result = Raise.fold(
            raise -> {
                throw new IllegalArgumentException("Some Exception");
            },
            throwable -> "Caught Exception: " + throwable.getMessage(),
            error -> "Recovered",
            resultValue -> resultValue + "!"
        );

        assertEquals("Caught Exception: Some Exception", result);
    }
}
