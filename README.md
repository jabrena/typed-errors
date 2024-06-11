# Either

*Either<L, R>* is a commonly used data type that encapsulates a value of one of two possible types. It represents a value that can be either an "error" (left) or a "success" (right). This is particularly useful for error handling and avoiding exceptions.

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
