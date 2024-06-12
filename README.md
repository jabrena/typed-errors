# Typed errors

## Introduction

The Java programming language was designed with Exceptions in mind as the way to handle events that disrupts the normal flow of a program's execution. These exceptions can occur during the runtime of a program and can be caused by various issues such as incorrect input, network problems, or hardware malfunctions.

Exceptions in Java are represented by objects from classes that extend the Throwable class. There are two main types of exceptions in Java: checked exceptions and unchecked exceptions. Checked exceptions are checked at compile time, while unchecked exceptions are not.

Handling exceptions properly is important for writing robust and maintainable Java programs. It helps in dealing with unexpected situations effectively and ensures that the program does not crash or terminate abruptly.

## Either

*Either<L, R>* is a commonly used data type that encapsulates a value of one of two possible types. It represents a value that can be either an "error" (left) or a "success" (right). This is particularly useful for error handling and avoiding exceptions.

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
./mvnw clean test -Dtest=Solution1Test


./mvnw prettier:write
```

## How to show the coverage on Codespaces?

```bash
# Step 1: Launch the webserver with the JACOCO Report
./mvnw clean verify

jwebserver -p 9000 -d "$(pwd)/target/site/jacoco/"
```

## References

- https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/

