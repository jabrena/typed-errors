package info.jab.examples;

import info.jab.util.result.Failure;
import info.jab.util.result.Result;
import info.jab.util.result.Success;
import java.net.URI;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ResultReadmeExamplesTest {

    private static final Logger logger = LoggerFactory.getLogger(ResultReadmeExamplesTest.class);

    @Test
    void showLearnAboutResult() {
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
    }
}
