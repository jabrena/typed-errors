package info.jab.sc;

import info.jab.fp.util.Either;
import info.jab.fp.util.Result;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class SC1Test {

    record UserInfo(Integer userId, String username) {}

    private UserInfo getUserInfo(Integer userId) {
        if (userId > 1) {
            throw new RuntimeException("Katakroker");
        }
        return new UserInfo(userId, "UserName");
    }

    enum SubsystemProblems {
        DATABASE_ERROR,
        COMPUTATION_ERROR,
    }

    private Either<SubsystemProblems, UserInfo> getUserInfo2(Integer userId) {
        if (userId > 1) {
            return Either.left(SubsystemProblems.DATABASE_ERROR);
        }
        UserInfo result = new UserInfo(userId, "UserName");
        return Either.right(result);
    }

    private Either<SubsystemProblems, UserInfo> getUserInfo3(Integer userId) {
        if (userId == 2) {
            return Either.left(SubsystemProblems.DATABASE_ERROR);
        } else if (userId > 2) {
            System.out.println("Computation with latency");
            delay(2000);
            return Either.left(SubsystemProblems.COMPUTATION_ERROR);
        }
        UserInfo result = new UserInfo(userId, "UserName");
        return Either.right(result);
    }

    private Result<UserInfo> getUserInfo4(Integer userId) {
        if (userId == 2) {
            return Result.failure(new RuntimeException("Katakroker"));
        } else if (userId > 2) {
            System.out.println("Computation with latency");
            delay(2000);
            return Result.failure(new RuntimeException("Katakroker"));
        }
        UserInfo result = new UserInfo(userId, "UserName");
        return Result.success(result);
    }

    private void delay(int param) {
        try {
            Thread.sleep(param);
        } catch (InterruptedException ex) {
            // Empty on purpose
        }
    }

    private List<Follower> getFollowers(UserInfo userInfo) {
        return List.of(new Follower("follower1"), new Follower("follower2"));
    }

    record Follower(String nombre) {}

    @Test
    void should_1_work() {
        try (var scope = new StructuredTaskScope<UserInfo>()) {
            Subtask<UserInfo> userInfoTask = scope.fork(() -> getUserInfo(1));

            scope.join();

            System.out.println(userInfoTask.state());
            final var userInfo = userInfoTask.get();
            System.out.println("User: " + userInfo);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Disabled
    @Test
    void should_2_work_multiple_tasks() {
        try (var scope = new StructuredTaskScope<>()) {
            Subtask<UserInfo> userInfoTask = scope.fork(() -> getUserInfo(1));
            Subtask<List<Follower>> mostFollowersTask = scope.fork(() -> getFollowers(userInfoTask.get()));

            scope.join();

            System.out.println(userInfoTask.state());
            final var userInfo = userInfoTask.get();
            System.out.println("User: " + userInfo);
            System.out.println(mostFollowersTask.state());
            System.out.println("Followers: " + mostFollowersTask.get());
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_3_not_work() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<UserInfo> userInfoTask = scope.fork(() -> getUserInfo(2));
            Subtask<List<Follower>> mostFollowersTask = scope.fork(() -> getFollowers(userInfoTask.get()));

            scope.join().throwIfFailed();

            System.out.println(userInfoTask.state());
            System.out.println(mostFollowersTask.state());
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_4_work_with_either() {
        try (var scope = new StructuredTaskScope<>()) {
            Subtask<Either<SubsystemProblems, UserInfo>> userInfoTask = scope.fork(() -> getUserInfo2(1));

            scope.join();

            System.out.println(userInfoTask.state());
            final var userInfo = userInfoTask.get().get();
            System.out.println("User: " + userInfo);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //TODO Talk about Either & Structural Concurrency
    @Test
    void should_5_work_with_either_error() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<Either<SubsystemProblems, UserInfo>> userInfoTask = scope.fork(() -> getUserInfo2(2));

            scope.join().throwIfFailed();

            System.out.println(userInfoTask.state());
            if (userInfoTask.get().isLeft()) {
                var result = userInfoTask.get().fold(Function.identity(), Function.identity());
                System.out.println(result);
            }
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_6_work_with_either_timeout() {
        var timeout = Instant.now().plus(Duration.ofSeconds(1));
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<Either<SubsystemProblems, UserInfo>> userInfoTask = scope.fork(() -> getUserInfo3(3));

            scope.joinUntil(timeout).throwIfFailed();
            System.out.println(userInfoTask.state());
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //TODO Talk about Either with Raise & Structural Concurrency
    @Test
    void should_6_work_with_either_and_raise_timeout() {
        var timeout = Instant.now().plus(Duration.ofSeconds(1));
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<Either<SubsystemProblems, Integer>> userInfoTask = scope.fork(() -> {
                return Either.either(raise -> {
                    var a = raise.bind(getUserInfo3(3));
                    var b = raise.bind(getUserInfo3(3));
                    var c = raise.bind(getUserInfo3(3));
                    return a.username.length() + b.username.length() + c.username.length();
                });
            });

            scope.joinUntil(timeout).throwIfFailed();
            System.out.println(userInfoTask.state());
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_8_work() {
        try (var scope = new StructuredTaskScope<Result<UserInfo>>()) {
            var userInfoTask = scope.fork(() -> getUserInfo4(1));

            scope.join();

            System.out.println(userInfoTask.state());
            final var userInfo = userInfoTask.get();
            System.out.println("User: " + userInfo);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_9_work() {
        try (var scope = new StructuredTaskScope<Result<UserInfo>>()) {
            Supplier<Result<UserInfo>> userInfoTask = scope.fork(() -> getUserInfo4(1));

            scope.join();

            //System.out.println(userInfoTask.get() .state());
            final var userInfo = userInfoTask.get();
            System.out.println("User: " + userInfo);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_10_work() {
        try (var scope = new CustomScopePolicies.ResultScope<UserInfo>()) {
            scope.fork(() -> getUserInfo(1));
            scope.fork(() -> getUserInfo(1));
            scope.fork(() -> getUserInfo(2));

            var results = scope.join();

            // @formatter:off
            System.out.println("Streaming");
            results.stream()
                .filter(Result::isSuccess)
                .map(Result::getValue)
                .map(Optional::get)
                .forEach(System.out::println);
            // @formatter:on

        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_11_work() {
        try (var scope = new StructuredTaskScope<>()) {
            var subTask1 = scope.fork(() -> getUserInfo2(1));
            var subTask2 = scope.fork(() -> getUserInfo2(1));
            var subTask3 = scope.fork(() -> getUserInfo2(2));

            scope.join();

            // @formatter:off
            System.out.println("Streaming");
            List.of(subTask1, subTask2, subTask3).stream()
                .filter(st -> st.state() == Subtask.State.SUCCESS)
                .map(Subtask::get)
                .filter(Either::isRight)
                .map(Either::get)
                .forEach(System.out::println);
            // @formatter:on

        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
