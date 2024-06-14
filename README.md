# Typed errors

[![Java CI](https://github.com/jabrena/typed-errors/actions/workflows/maven.yml/badge.svg)](https://github.com/jabrena/typed-errors/actions/workflows/maven.yml)

## Introduction

The Java programming language was designed with Exceptions in mind as the way to handle events that disrupts the normal flow of a program's execution. These exceptions can occur during the runtime of a program and can be caused by various issues such as incorrect input, network problems, or hardware malfunctions.

Exceptions in Java are represented by objects from classes that extend the Throwable class. There are two main types of exceptions in Java: checked exceptions and unchecked exceptions. Checked exceptions are checked at compile time, while unchecked exceptions are not.

Handling exceptions properly is important for writing robust and maintainable Java programs. It helps in dealing with unexpected situations effectively and ensures that the program does not crash or terminate abruptly.

## Goals

This repository tries to add some Abstractions to improve the error handling.

## How to build in local?

```bash
sdk env install
./mvnw clean verify
./mvnw clean test -Dtest=LatencyProblem01Test
jwebserver -p 9000 -d "$(pwd)/target/site/jacoco/"
./mvnw javadoc:javadoc
jwebserver -p 9001 -d "$(pwd)/target/site/apidocs/"

./mvnw prettier:write

./mvnw versions:display-property-updates
./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates
./mvnw dependency:tree
```

## Error handling features

### Either

*Either<L, R>* is a commonly used data type that encapsulates a value of one of two possible types. It represents a value that can be either an "error" (left) or a "success" (right). This is particularly useful for error handling and avoiding exceptions.

### Either examples

```java
public class Solution8 implements ISolution {

    private static final Logger logger = LoggerFactory.getLogger(Solution4.class);

    sealed interface ConnectionProblem permits 
        ConnectionProblem.InvalidURI, ConnectionProblem.InvalidConnection {
        record InvalidURI() implements ConnectionProblem {}
        record InvalidConnection() implements ConnectionProblem {}
    }

    private Either<ConnectionProblem, String> fetchWebsite(String address) {
        try {
            URI uri = new URI(address);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Either.right(response.body());
        } catch (URISyntaxException | IllegalArgumentException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            return Either.left(new ConnectionProblem.InvalidURI());
        } catch (IOException | InterruptedException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            return Either.left(new ConnectionProblem.InvalidConnection());
        }
    }

    @Override
    public String extractHTML(String address) {
        Either<ConnectionProblem, String> result = fetchWebsite(address);
        return switch (result) {
            case Either.Right<ConnectionProblem, String> right -> right.get();
            default -> "";
        };
    }
}
```

---

```java
enum ConnectionProblem {
    TIMEOUT,
    UNKNOWN,
}

Function<String, CompletableFuture<Either<ConnectionProblem, String>>> fetchAsyncEither = address -> {
    logger.info("Thread: {}", Thread.currentThread().getName());
    return CompletableFuture
        .supplyAsync(() -> SimpleCurl.fetch.andThen(SimpleCurl.log).apply(address), executor)
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

Function<List<String>, Stream<String>> fetchListAsyncEither = list -> {
    var futureRequests = list.stream()
        .map(fetchAsyncEither)
        .collect(toList());

    return futureRequests.stream()
        .map(CompletableFuture::join)
        .filter(Either::isRight)
        .map(Either::get)
        .flatMap(serialize); //Not safe code
};

```

### Either in other programming languages

- Haskell: https://hackage.haskell.org/package/base-4.20.0.1/docs/Data-Either.html
- Scala: https://scala-lang.org/api/3.x/scala/util/Either.html
- Kotlin: https://apidocs.arrow-kt.io/arrow-core/arrow.core/-either/index.html
- TS: https://gcanti.github.io/fp-ts/modules/Either.ts
- Golang: https://pkg.go.dev/github.com/asteris-llc/gofpher/either
- Rust: https://docs.rs/either/latest/either/enum.Either.html

## References

- https://github.com/jabrena/latency-problems
- https://github.com/jabrena/exceptions-in-java
- https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/
- https://fsharpforfunandprofit.com/rop/
- https://dev.to/anthonyjoeseph/either-vs-exception-handling-3jmg

