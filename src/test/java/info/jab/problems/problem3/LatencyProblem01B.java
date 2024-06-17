package info.jab.problems.problem3;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.jab.fp.util.Either;
import info.jab.problems.problem2.SimpleCurl;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Problem 1B
 * Ancient European peoples worshiped many gods like Greek, Roman & Nordic gods.
 * Every God is possible to be represented as the concatenation of every character converted in Decimal.
 * Zeus = 122101117115
 *
 * Load the list of Gods and find the sum of God names starting with the letter Z.
 *
 * Notes:
 * Every connection with any API has a Timeout of 2 seconds.
 * If in the process to load the list, the timeout is reached, the process will calculate with the rest of the lists.
 * REST API: https://my-json-server.typicode.com/jabrena/latency-problems
 */
public class LatencyProblem01B {

    private static final Logger logger = LoggerFactory.getLogger(LatencyProblem01B.class);

    private List<String> listOfGods;
    private int timeout;
    private ExecutorService executor;

    public LatencyProblem01B(List<String> listOfGods, int timeout) {
        this.listOfGods = listOfGods;
        this.timeout = timeout;

        this.executor = Executors.newFixedThreadPool(10);
    }

    Function<String, Stream<String>> serialize = param -> {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> deserializedData = objectMapper.readValue(param, new TypeReference<List<String>>() {});
            return deserializedData.stream();
        } catch (Exception ex) {
            logger.error("Bad Serialization process", ex);
            throw new RuntimeException(ex);
        }
    };

    Predicate<String> godStartingByn = s -> s.toLowerCase().charAt(0) == 'z';

    // @formatter:off
    Function<String, List<Integer>> toDigits = s -> s.chars()
        .mapToObj(is -> Integer.valueOf(is))
        .toList();

    Function<List<Integer>, String> concatDigits = li -> li.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(""));
    // @formatter:on

    final String defaultFetchError = "[\"FETCH_BAD_RESULT\"]";

    Function<String, CompletableFuture<String>> fetchAsync = address -> {
        logger.info("Thread: {}", Thread.currentThread().getName());
        return CompletableFuture.supplyAsync(() -> SimpleCurl.fetch.andThen(SimpleCurl.log).apply(address), executor)
            .exceptionally(ex -> {
                logger.warn(address, ex);
                return defaultFetchError;
            })
            .completeOnTimeout(defaultFetchError, timeout, TimeUnit.SECONDS);
    };

    Function<String, CompletableFuture<String>> fetchAsyncJ8 = address -> {
        logger.info("Thread: {}", Thread.currentThread().getName());
        return CompletableFuture.supplyAsync(() -> SimpleCurl.fetch.andThen(SimpleCurl.log).apply(address), executor).handle((response, ex) -> {
            if (!Objects.isNull(ex)) {
                logger.warn(address, ex);
                return defaultFetchError;
            }
            return response;
        });
    };

    Function<String, CompletableFuture<String>> fetchAsyncJ9 = address -> {
        logger.info("Thread: {}", Thread.currentThread().getName());
        return CompletableFuture.supplyAsync(() -> SimpleCurl.fetch.andThen(SimpleCurl.log).apply(address), executor)
            .orTimeout(timeout, TimeUnit.SECONDS)
            .handle((response, ex) -> {
                if (!Objects.isNull(ex)) {
                    logger.warn(address, ex);
                    return defaultFetchError;
                }
                return response;
            });
    };

    enum ConnectionProblem {
        TIMEOUT,
        UNKNOWN,
    }

    Function<String, CompletableFuture<Either<ConnectionProblem, String>>> fetchAsyncEither = address -> {
        logger.info("Thread: {}", Thread.currentThread().getName());
        return CompletableFuture.supplyAsync(() -> SimpleCurl.fetch.andThen(SimpleCurl.log).apply(address), executor)
            .orTimeout(timeout, TimeUnit.SECONDS)
            .handle((response, ex) -> {
                if (!Objects.isNull(ex)) {
                    logger.warn(address, ex);
                    return switch (ex) {
                        case java.util.concurrent.TimeoutException timeoutEx -> Either.left(ConnectionProblem.TIMEOUT);
                        default -> Either.left(ConnectionProblem.UNKNOWN);
                    };
                }
                return Either.right(response);
            });
    };

    // @formatter:off

    Function<List<String>, Stream<String>> fetchListAsync = s -> {
        List<CompletableFuture<String>> futureRequests = s.stream()
            .map(fetchAsyncJ9)
            .toList();

        return futureRequests.stream()
            .map(CompletableFuture::join)
            .flatMap(serialize); //Not safe code
    };

    Function<List<String>, Stream<String>> fetchListAsyncEither = list -> {
        var futureRequests = list.stream()
            .map(fetchAsyncEither)
            .toList();

        return futureRequests.stream()
            .map(CompletableFuture::join)
            .filter(Either::isRight)
            .map(Either::get)
            .flatMap(serialize); //Not safe code
    };

    Function<List<String>, Stream<String>> fetchListAsyncJ8 = s -> {
        List<CompletableFuture<String>> futureRequests = s.stream()
            .map(fetchAsyncJ8)
            .toList();

        return futureRequests
            .stream()
            .map(cf -> {
                try {
                    return cf.get(timeout, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return defaultFetchError;
                }
            })
            .flatMap(serialize);
    };

    Consumer<String> print = logger::info;

    Function<Stream<String>, Stream<String>> filterGods = ls -> ls
        .filter(godStartingByn)
        .peek(print);

    Function<Stream<String>, BigInteger> sum = ls ->
        ls.map(toDigits.andThen(concatDigits).andThen(BigInteger::new))
        .reduce(BigInteger.ZERO, (l1, l2) -> l1.add(l2));

    public BigInteger javaStreamSolution() {
        return fetchListAsync
            .andThen(filterGods)
            .andThen(sum)
            .apply(listOfGods);
    }

    public BigInteger java8StreamSolution() {
        return fetchListAsyncJ8
            .andThen(filterGods)
            .andThen(sum)
            .apply(listOfGods);
    }

    public BigInteger javaEitherSolution() {
        return fetchListAsyncEither
            .andThen(filterGods)
            .andThen(sum)
            .apply(listOfGods);
    }
    // @formatter:on
}
