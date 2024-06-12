package info.jab.problems.problem2;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCurl {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCurl.class);

    static Function<URL, String> fetch = url -> {
        try {
            logger.debug("Thread: {}", Thread.currentThread().getName());
            logger.debug("Requested URL: {}", url);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(url.toURI()).build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new RuntimeException("Bad Request", ex);
        }
    };

    static Function<URL, Optional<String>> fetch2 = url -> {
        try {
            logger.debug("Thread: {}", Thread.currentThread().getName());
            logger.debug("Requested URL: {}", url);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(url.toURI()).build();

            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return Optional.of(response);
        } catch (Exception ex) {
            logger.error("SimpleCURL Error: {}", ex.getLocalizedMessage(), ex);
            return Optional.empty();
        }
    };

    static Function<String, String> log = value -> {
        logger.debug("Response: {}", value);
        return value;
    };
}
