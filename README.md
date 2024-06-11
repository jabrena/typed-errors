# Either

## Either<L,R>

*Either<L, R>* is a commonly used data type that encapsulates a value of one of two possible types. It represents a value that can be either an "error" (left) or a "success" (right). This is particularly useful for error handling and avoiding exceptions.

### Example:

```java
public class Solution6 implements ISolution {

    private static final Logger logger = LoggerFactory.getLogger(Solution4.class);

    sealed interface ConnectionProblem permits InvalidURI, InvalidConnection {}

    record InvalidURI() implements ConnectionProblem {}

    record InvalidConnection() implements ConnectionProblem {}

    //The business logic is splitted in parts
    //Reducing the number of exceptions handling in the class
    @Override
    public String extractHTML(String address) {
        //Error handling handling in the origin
        Function<String, Either<ConnectionProblem, String>> toHTML = param -> {
            try {
                URI uri = new URI(param);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return Either.right(response.body());
            } catch (URISyntaxException | IllegalArgumentException ex) {
                logger.warn(ex.getLocalizedMessage(), ex);
                return Either.left(new InvalidURI());
            } catch (IOException | InterruptedException ex) {
                logger.warn(ex.getLocalizedMessage(), ex);
                return Either.left(new InvalidConnection());
            }
        };

        var result = toHTML.apply(address);
        return switch (result) {
            case Either.Right<ConnectionProblem, String> right -> right.value();
            default -> "";
        };
    }
}
```

### Who is using Either in Github?

- https://github.com/SeleniumHQ/selenium/
- https://github.com/bazelbuild/bazel/
- https://github.com/strimzi/strimzi-kafka-operator/
- https://github.com/camunda/camunda/
- https://github.com/awslabs/dqdl/

## How to build in local?

```bash
sdk env install
./mvnw clean verify
./mvnw clean compile exec:java -Dexec.mainClass="info.jab.jdk.ExceptionFinderExample"
./mvnw clean compile exec:java -Dexec.mainClass="info.jab.problems.Solution1"
./mvnw clean test -Dtest=ExceptionFinderTest#should_group_exceptions_by_javaModule
./mvnw clean test -Dtest=Solution1Test


./mvnw prettier:write
```
