package info.jab.sc;

import static org.assertj.core.api.Assertions.assertThat;

import info.jab.util.either.Either;

import java.util.concurrent.CancellationException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class StructuredTest {

    record UserInfo(Integer userId, String username) {}

    private UserInfo getUserInfo(Integer userId) {
        if (userId > 1) {
            throw new RuntimeException("Katakroker");
        }
        return new UserInfo(userId, "UserName");
    }

    @Test
    void should_1_work() {
        try (var scope = new StructuredTaskScope<UserInfo>()) {
            Subtask<UserInfo> subtask = scope.fork(() -> getUserInfo(1));

            scope.join();

            assertThat(subtask.state()).isEqualTo(Subtask.State.SUCCESS);
            final var userInfo = subtask.get();
            System.out.println("User: " + userInfo);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static <T, R, L> Either<L, R> taskScope1(Function<T, R> task, T param, L left) {
        try (var scope = new StructuredTaskScope<R>()) {
            Subtask<R> subtask = scope.fork(() -> task.apply(param));

            scope.join();

            return Either.right(subtask.get());
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        return Either.left(left);
    }

    @Test
    void should_2_work() {
        Function<Integer, UserInfo> task = param -> {
            return getUserInfo(param);
        };

        var result = taskScope1(task, 1, "Katakroker");
        assertThat(result.isRight()).isTrue();
    }

    static <T> T structured(Function<StructuredTaskScope<? super T>, ? extends T> block) {
        try (var scope = new StructuredTaskScope<T>()) {
            T result = block.apply(scope);
            scope.join();
            return result;
        } catch (InterruptedException ex) {
            throw new CancellationException(ex.getMessage());
        }
    }

    @Test
    void should_3_work() {
        UserInfo result = structured(scope -> {
            var fiber = scope.fork(() -> getUserInfo(1));
            return fiber.get();
        });
        assertThat(result).isNotNull();
    }
}
