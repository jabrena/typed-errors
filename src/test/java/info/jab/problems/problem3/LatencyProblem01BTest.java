package info.jab.problems.problem3;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LatencyProblem01BTest {

    WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    // @formatter:off
    private void loadStubs() {
        wireMockServer.stubFor(
            get(urlEqualTo("/greek"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("greek.json"))
        );

        //Forcing a timeout
        wireMockServer.stubFor(
            get(urlEqualTo("/roman"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withFixedDelay(5000)
                .withBodyFile("roman.json"))
        );

        wireMockServer.stubFor(
            get(urlEqualTo("/nordic"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("nordic.json"))
        );
    }

    // @formatter:on

    @Test
    public void should_solve_problem1() {
        //Given
        loadStubs();

        int timeout = 2;
        // @formatter:off
        List<String> listOfGods = List.of(
            "http://localhost:8090/greek", 
            "http://localhost:8090/roman", 
            "http://localhost:8090/nordic");
        // @formatter:on
        BigInteger expectedResult = new BigInteger("90101117115");

        //When
        LatencyProblem01B problem = new LatencyProblem01B(listOfGods, timeout);
        var result = problem.javaStreamSolution();

        //Then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void should_solve_problem_alternative2() {
        //Given
        loadStubs();

        int timeout = 2;
        // @formatter:off
        List<String> listOfGods = List.of(
            "http://localhost:8090/greek", 
            "http://localhost:8090/roman", 
            "http://localhost:8090/nordic");
        // @formatter:on
        BigInteger expectedResult = new BigInteger("90101117115");

        //When
        LatencyProblem01B problem = new LatencyProblem01B(listOfGods, timeout);
        var result = problem.javaEitherSolution();

        //Then
        assertThat(result).isEqualTo(expectedResult);
    }
}
