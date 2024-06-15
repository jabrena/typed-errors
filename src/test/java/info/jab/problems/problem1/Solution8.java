package info.jab.problems.problem1;

import info.jab.fp.util.Either;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solution8 implements ISolution {

    private static final Logger logger = LoggerFactory.getLogger(Solution8.class);

    sealed interface ConnectionProblem permits ConnectionProblem.InvalidURI, ConnectionProblem.InvalidConnection {
        record InvalidURI() implements ConnectionProblem {}

        record InvalidConnection() implements ConnectionProblem {}
    }

    // Error handling in the origin
    private Either<ConnectionProblem, String> fetchWebsite(String address) {
        try {
            URI uri = new URI(address);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Either.right(response.body());
        } catch (URISyntaxException | IllegalArgumentException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            return Either.left(new ConnectionProblem.InvalidURI());
        } catch (IOException | InterruptedException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            return Either.left(new ConnectionProblem.InvalidConnection());
        }
    }

    //Reducing the number of exceptions handling in the class
    @Override
    public String extractHTML(String address) {
        Either<ConnectionProblem, String> result = fetchWebsite(address);
        return switch (result) {
            case Either.Right<ConnectionProblem, String> right -> right.get();
            case Either.Left<ConnectionProblem, String> left -> "";
        };
    }
}
