package info.jab.problems.problem2;

import info.jab.util.either.Either;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCurl {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCurl.class);

    public static Function<String, String> fetch = address -> {
        try {
            logger.debug("Thread: {}", Thread.currentThread().getName());
            logger.debug("Requested URL: {}", address);

            URI uri = new URI(address);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            throw new RuntimeException("Bad Request", ex);
        }
    };

    public static Function<String, Optional<String>> fetchOptional = address -> {
        try {
            logger.debug("Thread: {}", Thread.currentThread().getName());
            logger.debug("Requested URL: {}", address);

            URI uri = new URI(address);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return Optional.of(response);
        } catch (Exception ex) {
            logger.error("SimpleCURL Error: {}", ex.getLocalizedMessage(), ex);
            return Optional.empty();
        }
    };

    public static Function<String, Either<String, String>> fetchEither = address -> {
        try {
            logger.debug("Thread: {}", Thread.currentThread().getName());
            logger.debug("Requested URL: {}", address);

            URI uri = new URI(address);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Either.right(response.body());
        } catch (URISyntaxException | IllegalArgumentException ex) {
            logger.warn(address);
            logger.warn(ex.getLocalizedMessage(), ex);
            return Either.left(ex.getLocalizedMessage());
        } catch (IOException | InterruptedException ex) {
            logger.warn(address);
            logger.warn(ex.getLocalizedMessage(), ex);
            return Either.left(ex.getLocalizedMessage());
        }
    };

    public static Function<String, String> log = value -> {
        logger.debug("Response: {}", value);
        return value;
    };
}
