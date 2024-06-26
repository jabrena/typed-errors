package info.jab.fp.util.result;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResultROPTest {

    Result<Integer> divide(int dividend, int divisor) {
        try {
            if (divisor == 0) {
                throw new IllegalArgumentException("Division by zero");
            }
            return Result.success(dividend / divisor);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    Result<Integer> parseInteger(String input) {
        try {
            int parsedValue = Integer.parseInt(input);
            return Result.success(parsedValue);
        } catch (NumberFormatException e) {
            return Result.failure(e);
        }
    }

    // @formatter:off

    Result<Integer> calculate(String input1, String input2) {
        return parseInteger(input1)
                .flatMap(value1 -> parseInteger(input2)
                .flatMap(value2 -> divide(value1, value2)));
    }

    // @formatter:on

    @Test
    void should_work_ok() {
        Result<Integer> result = calculate("10", "2");
        int finalValue = result.getOrElse(() -> 0);

        assertThat(finalValue).isEqualTo(5);
    }

    @Test
    void should_work_ko() {
        Result<Integer> result = calculate("0", "2");
        int finalValue = result.getOrElse(() -> 0);

        assertThat(finalValue).isZero();
    }
}
