# Typed errors

## Introduction

The Java programming language was designed with Exceptions in mind as the way to handle events that disrupts the normal flow of a program's execution. These exceptions can occur during the runtime of a program and can be caused by various issues such as incorrect input, network problems, or hardware malfunctions.

Exceptions in Java are represented by objects from classes that extend the Throwable class. There are two main types of exceptions in Java: checked exceptions and unchecked exceptions. Checked exceptions are checked at compile time, while unchecked exceptions are not.

Handling exceptions properly is important for writing robust and maintainable Java programs. It helps in dealing with unexpected situations effectively and ensures that the program does not crash or terminate abruptly.

## Goals

This repository tries to add some Abstractions to improve the error handling.

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

### Either in other programming languages

- Haskell: https://hackage.haskell.org/package/base-4.20.0.1/docs/Data-Either.html
- Scala: https://scala-lang.org/api/3.x/scala/util/Either.html
- Kotlin: https://apidocs.arrow-kt.io/arrow-core/arrow.core/-either/index.html
- TS: https://gcanti.github.io/fp-ts/modules/Either.ts
- Golang: https://pkg.go.dev/github.com/asteris-llc/gofpher/either
- Rust: https://docs.rs/either/latest/either/enum.Either.html

## How to build in local?

```bash
sdk env install
./mvnw clean verify
./mvnw clean test -Dtest=SolutionTest
jwebserver -p 9000 -d "$(pwd)/target/site/jacoco/"


./mvnw prettier:write
```

## References

- https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/
- https://fsharpforfunandprofit.com/rop/
- https://dev.to/anthonyjoeseph/either-vs-exception-handling-3jmg

