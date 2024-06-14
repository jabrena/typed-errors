package info.jab.problems.problem1;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solution3 implements ISolution {

    private static final Logger logger = LoggerFactory.getLogger(Solution3.class);

    //The business logic is splitted in parts
    //Some functions or methods raise generic exceptions
    @Override
    public String extractHTML(String address) {
        Function<String, URI> toUri = param -> {
            try {
                return new URI(param);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Bad parameter");
            }
        };

        Function<URI, String> toHTML = param -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(param).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (IOException | InterruptedException | IllegalArgumentException ex) {
                logger.warn(ex.getLocalizedMessage(), ex);
                return "";
            }
        };

        return toUri.andThen(toHTML).apply(address);
    }
}
