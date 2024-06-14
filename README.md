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
./mvnw clean test -Dtest=EitherMonadTest
jwebserver -p 9000 -d "$(pwd)/target/site/jacoco/"
./mvnw javadoc:javadoc
./mvnw verify -DskipTests -P post-javadoc
jwebserver -p 9001 -d "$(pwd)/docs/javadocs/"


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

### Who is using Either?

- https://github.com/SeleniumHQ/selenium/
- https://github.com/bazelbuild/bazel/
- https://github.com/strimzi/strimzi-kafka-operator/
- https://github.com/camunda/camunda/
- https://github.com/awslabs/dqdl/
- https://github.com/mongodb/mongo-java-driver/
- https://github.com/kestra-io/kestra/
- https://github.com/pulumi/pulumi-java/
- https://github.com/mulesoft/mule/

### Result< T >

A utility class representing a computation that may either result in a value (success) or an exception (failure).

### Result examples

```java
public class ResultExample {

    public static void main(String[] args) {
        List<String> endpoints = Arrays.asList(
            "https://jsonplaceholder.typicode.com/posts/1",
            "https://jsonplaceholder.typicode.com/posts/2",
            "https://jsonplaceholder.typicode.com/posts/3"
        );

        // @formatter:off

        List<Result<String>> results = endpoints.stream()
            .map(ResultExample::fetchData)
            .toList();

        List<String> successfulResults = results.stream()
            .filter(Result::isSuccess)
            .map(Result::getValue)
            .flatMap(Optional::stream)
            .toList();

        // @formatter:on

        successfulResults.forEach(System.out::println);
    }

    private static Result<String> fetchData(String endpoint) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endpoint)).build();

        return Result.runCatching(() -> {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new IOException("Failed to fetch data from " + endpoint);
            }
        });
    }
}
```

### Result in other programming languages

- Kotlin: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/
- Rust: https://doc.rust-lang.org/std/result/
- Ocaml: https://ocaml.org/manual/5.2/api/Result.html

## References

- https://github.com/jabrena/latency-problems
- https://github.com/jabrena/exceptions-in-java
- https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/
- https://fsharpforfunandprofit.com/rop/
- https://dev.to/anthonyjoeseph/either-vs-exception-handling-3jmg

