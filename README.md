# Typed errors

[![Java CI](https://github.com/jabrena/typed-errors/actions/workflows/maven.yml/badge.svg)](https://github.com/jabrena/typed-errors/actions/workflows/maven.yml)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=jabrena_typed-errors)

## Cloud IDEs

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/jabrena/typed-errors)

[![](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/jabrena/typed-errors)

## How to build in local?

```bash
sdk env install
./mvnw prettier:write

./mvnw clean verify 
./mvnw clean test -Dtest=StructuredTest

//Code coverage
./mvnw clean verify jacoco:report
jwebserver -p 9000 -d "$(pwd)/target/site/jacoco/"
./mvnw clean verify org.pitest:pitest-maven:mutationCoverage

//Javadoc
./mvnw clean compile javadoc:javadoc
./mvnw verify -DskipTests -P post-javadoc
jwebserver -p 9001 -d "$(pwd)/docs/javadocs/"

./mvnw versions:display-property-updates
./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates
./mvnw dependency:tree
```

## Introduction

The Java programming language was designed with Exceptions in mind as the way to handle events that disrupts the normal flow of a program's execution. These exceptions can occur during the runtime of a program and can be caused by various issues such as incorrect input, network problems, or hardware malfunctions.

Exceptions in Java are represented by objects from classes that extend the Throwable class. There are two main types of exceptions in Java: checked exceptions and unchecked exceptions. Checked exceptions are checked at compile time, while unchecked exceptions are not.

Handling exceptions properly is important for writing robust and maintainable Java programs. It helps in dealing with unexpected situations effectively and ensures that the program does not crash or terminate abruptly.

## Problem statement

Using the following Gherkin feature for educational purposes and the different implementations, they will show the current Java modelling problem without using Typed Errors and how this library could help you to design & implement better software.

```gherkin
Feature: Convert String into Integer

  # Happy path
  Scenario: The user introduce valid String values
    Given a String as a parameter
    When when it is passed to the method to convert String into Integer
    Then it returns a valid Integer
    
    Examples
    | input | output |
    |  "1"  |   1    |
    |  "2"  |   2    |
    | "-1"  |  -1    |

  # Unhappy path: ASCII Characters
  Scenario: Introducing ASCII characters
    Given a String as a parameter
    When when it is passed to the method to convert String into Integer
    Then returns an Exception

    Examples
    | input | output |
    |  "A"  |  KO    |
    |  "B"  |  KO    |
    |  "z"  |  KO    |

  # Unhappy path: Symbols
  Scenario: Introducing symbols
    Given a String as a parameter
    When when it is passed to the method to convert String into Integer
    Then returns an Exception

    Examples
    | input | output |
    |  "."  |  KO    |
    |  ","  |  KO    |
    |  "!"  |  KO    |

  # Unhappy path: Numbers out of Integer Data Type range (-2^31-1 <--> 2^31-1)
  Scenario: Reaching the limit of Integer
    Given a String as a parameter
    When when it is passed to the method to convert String into Integer
    Then returns an Exception

    Examples
    | input          | output |
    |  "2147483648"  |  KO    |
    |  "-2147483649" |  KO    |

```

After reading the the **Specification**, you could implement in the following way in Java in an initial way:

```java
Function<String, Integer> parseInt = param -> {
    try {
        return Integer.parseInt(param);
    } catch (NumberFormatException ex) {
        logger.warn(ex.getMessage(), ex);
        return -99;
    }
};
```

But in this case, the implementation adds side effects in case of the user introduce a non positive numbers or larger negative numbers than -99.

---

So maybe you could use Java Exceptions, as another implementation:

```java
Function<String, Integer> parseInt2 = param -> {
    try {
        return Integer.parseInt(param);
    } catch (NumberFormatException ex) {
        logger.warn(ex.getMessage(), ex);
        throw new RuntimeException("Katakroker", ex);
    }
};
```

But using this way, the signature changes because in some cases, the implementation trigger an Exception and it could be considered as a another way of **GOTO**.

---

In `Java 8`, the lenguage evolved and it included Wrapper Types like `Optional` which it is valid to describe that the result could be present or not. 

One possible implementation could be:

```java
Function<String, Optional<Integer>> parseInt3 = param -> {
    try {
        return Optional.of(Integer.parseInt(param));
    } catch (NumberFormatException ex) {
        logger.warn(ex.getMessage(), ex);
        return Optional.empty();
    }
};
```

But reviewing the implementation, `Optional` was not designed to model Errors, it was modelled to describe the presence or absence of a result.

---

Finally, you could consider to use other kind of `Wrapper Types` to describe that your method/function could return a valid result or an error. 

This approach is very common in plenty programming languages like: `Scala`, `Kotlin`, `TypeScript`, `Rust`, `Golang`, `Swift`, `Haskell`, `Unison`, `Ocaml` or `F#`.

So, using the prevoius idea to model your method to return a value or an error, you could implement with **Either** in this way:

```java
enum ConversionIssue {
    BAD_STRING,
}

Function<String, Either<ConversionIssue, Integer>> parseInt4 = param -> {
    try {
        return Either.right(Integer.parseInt(param));
    } catch (NumberFormatException ex) {
        logger.warn(ex.getMessage(), ex);
        return Either.left(ConversionIssue.BAD_STRING);
    }
};
```

or using **Result**:

```java
Function<String, Result<Integer>> parseInt5 = param -> {
    return Result.runCatching(() -> {
        return Integer.parseInt(param);
    });
};
```

So, if you followed the previous examples and you understood the concepts behind them, now you understand the purpose of this Java library.

## Library Goal

A Java library to help developers on `Error handling` using functional programming techniques and new Java Types.

## Error types

### *Either<L, R>*

*Either<L, R>* is a commonly used data type that encapsulates a value of one of two possible types. It represents a value that can be either an "error" (left) or a "success" (right). This is particularly useful for error handling and avoiding exceptions.

### Either examples

```java
//1. Learn to instanciate an Either object.
enum ConnectionProblem {
    INVALID_URL,
    INVALID_CONNECTION,
}

Either<ConnectionProblem, String> resultLeft = Either.left(ConnectionProblem.INVALID_URL);
Either<ConnectionProblem, String> resultRight = Either.right("Success");

Either<ConnectionProblem, String> eitherLeft = new Left<>(ConnectionProblem.INVALID_CONNECTION);
Either<ConnectionProblem, String> eitherRight = new Right<>("Success");

//2. Learn to use Either to not propagate Exceptions any more
Function<String, Either<ConnectionProblem, URI>> toURI = address -> {
    try {
        var uri = new URI(address);
        return Either.right(uri);
    } catch (URISyntaxException | IllegalArgumentException ex) {
        logger.warn(ex.getLocalizedMessage(), ex);
        return Either.left(ConnectionProblem.INVALID_URL);
    }
};

//3. Process results
Function<Either<ConnectionProblem, URI>, String> process = param -> {
    return switch (param) {
        case Right<ConnectionProblem, URI> right -> right.get().toString();
        case Left<ConnectionProblem, URI> left -> "";
    };
};

var case1 = "https://www.juanantonio.info";
var result = toURI.andThen(process).apply(case1);
System.out.println("Result: " + result);

Function<Either<ConnectionProblem, URI>, String> process2 = param -> {
    return param.fold(l -> "", r -> r.toString());
};

var case2 = "https://";
var result2 = toURI.andThen(process2).apply(case2);
System.out.println("Result: " + result2);

//Guarded patterns
//any guarded pattern makes the switch statement non-exhaustive
Function<Either<ConnectionProblem, URI>, String> process3 = param -> {
    return switch (param) {
        case Either e when e.isRight() -> e.get().toString();
        default -> "";
    };
};

var case3 = "https://www.juanantonio.info";
var result3 = toURI.andThen(process3).apply(case3);
System.out.println("Result: " + result3);

//4. Railway-oriented programming

Function<String, Either<String, String>> validateTopLevelDomain = email -> {
    String tld = email.substring(email.lastIndexOf('.') + 1);
    if (tld.length() != 3) {
        return Either.left("Invalid top-level domain");
    }
    return Either.right(email);
};

Function<String, Either<String, String>> validateUsername = email -> {
    String username = email.substring(0, email.indexOf('@'));
    if (username.length() < 5) {
        return Either.left("Username must be at least 5 characters");
    }
    return Either.right(email);
};

Function<String, Either<String, String>> validateDomain = email -> {
    String domain = email.substring(email.indexOf('@') + 1);
    if (!domain.contains(".")) {
        return Either.left("Invalid domain format");
    }
    return Either.right(email);
};

// @formatter:off

Function<String, Either<String, String>> validateEmail = email -> {
    return validateUsername.apply(email)
        .flatMap(validUsername -> validateDomain.apply(email))
        .flatMap(validDomain -> validateTopLevelDomain.apply(email));
};

// @formatter:on

String email = "john.doe@example.com";
Either<String, String> result4 = validateEmail.apply(email);

assertTrue(result4.isRight());
assertEquals(email, result4.get());

String email2 = "jd@example.com";
Either<String, String> result5 = validateEmail.apply(email2);

assertTrue(result5.isLeft());
```

### Either in other programming languages

- Haskell: https://hackage.haskell.org/package/base-4.20.0.1/docs/Data-Either.html
- Scala: https://scala-lang.org/api/3.x/scala/util/Either.html
- Kotlin: https://apidocs.arrow-kt.io/arrow-core/arrow.core/-either/index.html
- TS: https://gcanti.github.io/fp-ts/modules/Either.ts
- Golang: https://pkg.go.dev/github.com/samber/mo#Either
- Rust: https://docs.rs/either/latest/either/enum.Either.html
- Unison: https://www.unison-lang.org/docs/fundamentals/control-flow/exception-handling/

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

### *Result< T>*

*Result< T >* represents a computation that may either result in a value (success) or an exception (failure).

### Result examples

```java
//1. Learn to instanciate an Either object.
var resultLeft = Result.failure(new RuntimeException("Katakroker"));
var resultRight = Result.success("Success");

var result2Left = new Failure<>(new RuntimeException("Katakroker"));
var result2Right = new Success<>("Success");

//2. Learn to use Either to not propagate Exceptions any more
Function<String, Result<URI>> toURI = address -> {
    return Result.runCatching(() -> {
        return new URI(address);
    });
};

//3. Process results
Function<Result<URI>, String> process = param -> {
    return switch (param) {
        case Success<URI> success -> success.value().toString();
        case Failure ko -> "";
    };
};

var case1 = "https://www.juanantonio.info";
var result = toURI.andThen(process).apply(case1);
System.out.println("Result: " + result);

// @formatter:off

Function<Result<URI>, String> process2 = param -> {
    return param.fold(
        "",
        onSuccess -> param.getValue().get().toString()
    );
};

// @formatter:on

var case2 = "https://";
var result2 = toURI.andThen(process2).apply(case2);
System.out.println("Result: " + result2);
```

### Result in other programming languages

- Kotlin: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/
- Rust: https://doc.rust-lang.org/std/result/
- Swift: https://developer.apple.com/documentation/swift/result
- Ocaml: https://ocaml.org/manual/5.2/api/Result.html
- F#: https://fsharp.github.io/fsharp-core-docs/reference/fsharp-core-fsharpresult-2.html

## References

- https://github.com/jabrena/latency-problems
- https://github.com/jabrena/exceptions-in-java
- https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/
- https://fsharpforfunandprofit.com/rop/
- https://www.thoughtworks.com/en-us/insights/blog/either-data-type-alternative-throwing-exceptions
- https://blog.rockthejvm.com/functional-error-handling-in-kotlin/
- https://blog.rockthejvm.com/functional-error-handling-in-kotlin-part-2/
- https://blog.rockthejvm.com/functional-error-handling-in-kotlin-part-3/
- https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/concurrent/StructuredTaskScope.java
- https://www.oracle.com/technical-resources/articles/enterprise-architecture/effective-exceptions-part1.html
- https://web.archive.org/web/20140430044213/http://c2.com/cgi-bin/wiki?DontUseExceptionsForFlowControl
- https://homepages.cwi.nl/~storm/teaching/reader/Dijkstra68.pdf
- https://cucumber.io/docs/gherkin/reference/

Made with ❤️ from Spain