package info.jab.examples;

import info.jab.fp.util.Result;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ResultExampleTest {

    @Test
    void should_work() {
        List<String> endpoints = Arrays.asList(
            "https://jsonplaceholder.typicode.com/posts/1",
            "https://jsonplaceholder.typicode.com/posts/2",
            "https://jsonplaceholder.typicode.com/posts/3"
        );

        // @formatter:off

        List<Result<String>> results = endpoints.stream()
            .map(ResultExampleTest::fetchData)
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

        return Result.mapCatching(() -> {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new IOException("Failed to fetch data from " + endpoint);
            }
        });
    }
}
