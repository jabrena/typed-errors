package info.jab.util.either;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EitherROPTest {

    Either<String, Integer> divide(int dividend, int divisor) {
        if (divisor == 0) {
            return Either.left("Division by zero");
        } else {
            return Either.right(dividend / divisor);
        }
    }

    Either<String, Integer> parseInteger(String input) {
        try {
            int parsedValue = Integer.parseInt(input);
            return Either.right(parsedValue);
        } catch (NumberFormatException e) {
            return Either.left("Invalid integer format");
        }
    }

    // @formatter:off

    Either<String, Integer> calculate(String input1, String input2) {
        return parseInteger(input1)
            .flatMap(value1 -> parseInteger(input2)
            .flatMap(value2 -> divide(value1, value2)));
    }

    // @formatter:on

    @Test
    void should_work_ok() {
        Either<String, Integer> result = calculate("10", "2");
        int finalValue = result.getOrElse(() -> 0);

        assertThat(finalValue).isEqualTo(5);
    }

    @Test
    void should_work_ko() {
        Either<String, Integer> result = calculate("0", "2");
        int finalValue = result.getOrElse(() -> 0);

        assertThat(finalValue).isZero();
    }
}
