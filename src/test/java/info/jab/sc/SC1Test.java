package info.jab.sc;

import info.jab.fp.util.Either;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class SC1Test {

    @Test
    void should_1_work() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<UserInfo> userInfoTask = scope.fork(() -> getUserInfo(1));

            scope.join().throwIfFailed();

            System.out.println(userInfoTask.state());
            final var userInfo = userInfoTask.get();
            System.out.println("User: " + userInfo);
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_2_work_multiple_tasks() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<UserInfo> userInfoTask = scope.fork(() -> getUserInfo(1));
            Subtask<List<Follower>> mostFollowersTask = scope.fork(() -> getFollowers(userInfoTask.get()));

            scope.join().throwIfFailed();

            System.out.println(userInfoTask.state());
            final var userInfo = userInfoTask.get();
            System.out.println("User: " + userInfo);
            System.out.println(mostFollowersTask.state());
            System.out.println("Followers: " + mostFollowersTask.get());
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_3_not_work() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<UserInfo> userInfoTask = scope.fork(() -> getUserInfo(2));

            scope.join().throwIfFailed();

            System.out.println(userInfoTask.state());
            final var userInfo = userInfoTask.get();
            System.out.println("User: " + userInfo);
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_4_work_with_either() {
        try (var scope = new StructuredTaskScope()) {
            Subtask<Either<SubsystemProblems, UserInfo>> userInfoTask = scope.fork(() -> getUserInfo2(1));

            scope.join();

            System.out.println(userInfoTask.state());
            final var userInfo = userInfoTask.get().get();
            System.out.println("User: " + userInfo);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_5_work_with_either_error() {
        try (var scope = new StructuredTaskScope()) {
            Subtask<Either<SubsystemProblems, UserInfo>> userInfoTask = scope.fork(() -> getUserInfo2(2));

            scope.join();

            System.out.println(userInfoTask.state());
            if (userInfoTask.get().isLeft()) {
                var result = userInfoTask.get().fold(ex -> ex.toString(), ok -> ok);
                System.out.println(result);
            }
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void should_6_work_with_either_timeout() {
        try (var scope = new StructuredTaskScope()) {
            Subtask<Either<SubsystemProblems, UserInfo>> userInfoTask = scope.fork(() -> getUserInfo3(3));

            try {
                scope.joinUntil(Instant.now().plus(Duration.ofMillis(1000)));
            } catch (TimeoutException e) {
                //
            }

            System.out.println(userInfoTask.state());
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

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

    private void delay(int param) {
        try {
            Thread.sleep(param);
        } catch (InterruptedException ex) {
            // Empty on purpose
        }
    }

    record UserInfo(Integer userId, String username) {}

    private List<Follower> getFollowers(UserInfo userInfo) {
        return List.of(new Follower("follower1"), new Follower("follower2"));
    }

    record Follower(String nombre) {}
}
