package info.jab.problems;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solution1 implements ISolution {

    private static final Logger logger = LoggerFactory.getLogger(Solution1.class);

    //Monolith approach, everything in the same method with one top Exception class
    @Override
    public String extractHTML(String address) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(address)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            return "";
        }
    }
}
