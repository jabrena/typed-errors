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

public class Solution5 implements ISolution {

    private static final Logger logger = LoggerFactory.getLogger(Solution5.class);

    //Reducing the number of exceptions handling in the class
    @Override
    public String extractHTML(String address) {
        Function<String, String> toHTML = param -> {
            try {
                URI uri = new URI(param);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (IOException | InterruptedException | URISyntaxException | IllegalArgumentException ex) {
                logger.warn(ex.getLocalizedMessage(), ex);
                return "";
            }
        };

        return toHTML.apply(address);
    }
}
