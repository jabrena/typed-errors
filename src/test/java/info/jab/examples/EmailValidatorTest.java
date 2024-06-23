package info.jab.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import info.jab.fp.util.Either;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class EmailValidatorTest {

    // @formatter:off

    private static Either<String, String> validateEmail(String email) {
        return validateUsername(email)
            .flatMap(validUsername -> validateDomain(email))
            .flatMap(validDomain -> validateTopLevelDomain(email));
    }

    // @formatter:on

    private static Either<String, String> validateUsername(String email) {
        String username = email.substring(0, email.indexOf('@'));
        if (username.length() < 5) {
            return Either.left("Username must be at least 5 characters");
        }
        return Either.right(email);
    }

    private static Either<String, String> validateDomain(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        if (!domain.contains(".")) {
            return Either.left("Invalid domain format");
        }
        return Either.right(email);
    }

    private static Either<String, String> validateTopLevelDomain(String email) {
        String tld = email.substring(email.lastIndexOf('.') + 1);
        if (tld.length() != 3) {
            return Either.left("Invalid top-level domain");
        }
        return Either.right(email);
    }

    @Test
    void testValidEmail() {
        String email = "john.doe@example.com";
        Either<String, String> result = validateEmail(email);

        assertTrue(result.isRight());
        assertEquals(email, result.get());
    }

    @Test
    void testInvalidUsername() {
        String email = "jd@example.com"; // Username is too short
        Either<String, String> result = validateEmail(email);

        assertTrue(result.isLeft());
        assertEquals("Username must be at least 5 characters", result.fold(Function.identity(), Function.identity()));
    }

    @Test
    void testInvalidDomain() {
        String email = "john.doe@com"; // Missing dot in the domain part
        Either<String, String> result = validateEmail(email);

        assertTrue(result.isLeft());
        assertEquals("Invalid domain format", result.fold(Function.identity(), Function.identity()));
    }

    @Test
    void testInvalidTopLevelDomain() {
        String email = "john.doe@example.c"; // TLD is too short
        Either<String, String> result = validateEmail(email);

        assertTrue(result.isLeft());
        assertEquals("Invalid top-level domain", result.fold(Function.identity(), Function.identity()));
    }
}
