package info.jab.problems.problem1;

import info.jab.fp.util.either.Either;
import info.jab.fp.util.either.Left;
import info.jab.fp.util.either.Right;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solution7 implements ISolution {

    private static final Logger logger = LoggerFactory.getLogger(Solution7.class);

    sealed interface ConnectionProblem permits InvalidURI, InvalidConnection {}

    record InvalidURI() implements ConnectionProblem {}

    record InvalidConnection() implements ConnectionProblem {}

    //Reducing the number of exceptions handling in the class
    @Override
    public String extractHTML(String address) {
        //Error handling handling in the origin
        Either<ConnectionProblem, String> result;
        try {
            URI uri = new URI(address);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = Either.right(response.body());
        } catch (URISyntaxException | IllegalArgumentException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            result = Either.left(new InvalidURI());
        } catch (IOException | InterruptedException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            result = Either.left(new InvalidConnection());
        }

        return switch (result) {
            case Right<ConnectionProblem, String> right -> right.value();
            case Left<ConnectionProblem, String> left -> "";
        };
    }
}
