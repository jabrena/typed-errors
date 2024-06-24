package info.jab.concepts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import info.jab.fp.util.Either;
import info.jab.fp.util.Result;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodSignatureTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodSignatureTest.class);

    Function<String, Integer> parseInt = param -> {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            return -99;
        }
    };

    Function<String, Integer> parseInt2 = param -> {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            throw new RuntimeException("Katakroker", ex);
        }
    };

    Function<String, Optional<Integer>> parseInt3 = param -> {
        try {
            return Optional.of(Integer.parseInt(param));
        } catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    };

    enum ConversionIssue {
        BAD_STRING,
    }

    Function<String, Either<ConversionIssue, Integer>> parseInt4 = param -> {
        try {
            return Either.right(Integer.parseInt(param));
        } catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            return Either.left(ConversionIssue.BAD_STRING);
        }
    };

    Function<String, Result<Integer>> parseInt5 = param -> {
        return Result.runCatching(() -> {
            return Integer.parseInt(param);
        });
    };

    // @formatter:off
    private static List<String> generateNumbers() {
        return IntStream.rangeClosed(-100, 100)
            .mapToObj(String::valueOf)
            .toList();
    }

    // @formatter:on

    @ParameterizedTest
    @MethodSource("generateNumbers")
    void should_parse_valid_integers(String input) {
        int result = parseInt.apply(input);
        assertThat(result + "").isEqualTo(input);
    }

    @ParameterizedTest
    @MethodSource("generateNumbers")
    void should_parse_valid_integers_v2(String input) {
        int result = parseInt2.apply(input);
        assertThat(result + "").isEqualTo(input);
    }

    @ParameterizedTest
    @MethodSource("generateNumbers")
    void should_parse_valid_integers_v3(String input) {
        var result = parseInt3.apply(input);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get() + "").isEqualTo(input);
    }

    @ParameterizedTest
    @MethodSource("generateNumbers")
    void should_parse_valid_integers_v4(String input) {
        var result = parseInt4.apply(input);
        assertThat(result.isRight()).isTrue();
        assertThat(result.get() + "").isEqualTo(input);
    }

    @ParameterizedTest
    @MethodSource("generateNumbers")
    void should_parse_valid_integers_v5(String input) {
        var result = parseInt5.apply(input);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().get() + "").isEqualTo(input);
    }

    // @formatter:off
    public static List<String> getNonLetterChars() {
        final char[] punctuation = {'.', ',', '!', '@', '#', '$', '%', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', '\\', '|', ';', ':', '"', '\''};
        
        return Arrays.asList(punctuation).stream()
                .map(String::valueOf)
                .toList();
    }

    // @formatter:on

    @ParameterizedTest
    @MethodSource("getNonLetterChars")
    void should_not_work_for_non_valid_integers(String input) {
        int expectedReslut = -99;
        int result = parseInt.apply(input);
        assertThat(result).isEqualTo(expectedReslut);
    }

    @ParameterizedTest
    @MethodSource("getNonLetterChars")
    void should_not_work_for_non_valid_integers_v2(String input) {
        assertThrows(RuntimeException.class, () -> parseInt2.apply(input), "Katakroker");
    }

    @ParameterizedTest
    @MethodSource("getNonLetterChars")
    void should_work_for_non_valid_integers_with_optional(String input) {
        var result = parseInt3.apply(input);
        assertThat(result.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("getNonLetterChars")
    void should_work_for_non_valid_integers_with_either(String input) {
        var result = parseInt4.apply(input);
        assertThat(result.isLeft()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("getNonLetterChars")
    void should_work_for_non_valid_integers_with_result(String input) {
        var result = parseInt5.apply(input);
        assertThat(result.isFailure()).isTrue();
    }
}
