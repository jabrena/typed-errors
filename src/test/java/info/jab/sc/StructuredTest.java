package info.jab.sc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestMethodOrder(MethodOrderer.MethodName.class)
class StructuredTest {

    private static final Logger logger = LoggerFactory.getLogger(StructuredTest.class);

    record UserInfo(Integer userId, String username) {}

    private UserInfo getUserInfo(Integer userId) {
        logger.info("Param: " + userId);
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

    static <T> T taskScope1(Supplier<T> task) {
        try (var scope = new StructuredTaskScope<T>()) {
            logger.info("Running tasks");
            Subtask<T> subtask = scope.fork(() -> task.get());
            scope.join();
            return subtask.get();
        } catch (InterruptedException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            throw new CancellationException(ex.getMessage());
        }
    }

    @Test
    void should_2_work() {
        Supplier<UserInfo> task2 = () -> getUserInfo(1);

        var result = taskScope1(task2);
        assertThat(result.userId).isEqualTo(1);
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

    @Disabled
    @Test
    void should_3_work() {
        UserInfo result = structured(scope -> {
            var fiber1 = scope.fork(() -> getUserInfo(1));
            return fiber1.get();
        });
        assertThat(result).isNotNull();
    }

    static <T> T taskScope2(Supplier<T> task1, Supplier<T> task2) {
        try (var scope = new StructuredTaskScope<T>()) {
            logger.info("Running tasks");
            Subtask<T> subtask1 = scope.fork(() -> task1.get());
            Subtask<T> subtask2 = scope.fork(() -> task2.get());
            scope.join();
            return subtask1.get();
        } catch (InterruptedException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            throw new CancellationException(ex.getMessage());
        }
    }

    @Test
    void should_4_work() {
        Supplier<UserInfo> task2 = () -> getUserInfo(1);

        var result = taskScope2(task2, task2);
        assertThat(result.userId).isEqualTo(1);
    }

    @Test
    void should_5_work() {
        Supplier<UserInfo> taskOk = () -> getUserInfo(1);
        Supplier<UserInfo> taskKo = () -> getUserInfo(2);
        Function<List<Subtask<UserInfo>>, UserInfo> reduce = subtaskList -> {
            subtaskList.stream().map(st -> st.state()).forEach(System.out::println);
            return subtaskList.get(0).get();
        };
        List<Supplier<UserInfo>> myArray = List.of(taskOk, taskOk);
        var result = taskScopeN(myArray, reduce);
        assertThat(result.userId).isEqualTo(1);
    }

    static <T> T taskScopeN(List<Supplier<T>> tasks, Function<List<Subtask<T>>, T> reduce) {
        try (var scope = new StructuredTaskScope<T>()) {
            List<Subtask<T>> subtaskList = new ArrayList<>(tasks.size());
            AtomicInteger counter = new AtomicInteger();
            logger.info("Running tasks");
            for (Supplier<T> task : tasks) {
                var subTask = scope.fork(() -> task.get());
                subtaskList.add(counter.getAndIncrement(), subTask);
            }
            scope.join();
            return reduce.apply(subtaskList);
        } catch (InterruptedException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            throw new CancellationException(ex.getMessage());
        }
    }

    @Test
    void should_6_work() {
        Supplier<UserInfo> taskOk = () -> getUserInfo(1);
        Supplier<UserInfo> taskKo = () -> getUserInfo(2);
        Function<List<Subtask<UserInfo>>, UserInfo> reduce = subtaskList -> {
            subtaskList.stream().map(st -> st.state()).forEach(System.out::println);
            return subtaskList.get(0).get();
        };
        List<Supplier<UserInfo>> myArray = List.of(taskOk, taskOk, taskKo);
        var result = taskScopeN(myArray, reduce);
        assertThat(result.userId).isEqualTo(1);
    }
}
