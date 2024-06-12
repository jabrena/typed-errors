package info.jab.problems.problem1;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solution4 implements ISolution {

    private static final Logger logger = LoggerFactory.getLogger(Solution4.class);

    //The business logic is splitted in parts
    //Reducing the number of exceptions handling in the class
    @Override
    public String extractHTML(String address) {
        //Over engineering example
        Function<String, URI> toUri = param -> {
            return URI.create(param);
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
