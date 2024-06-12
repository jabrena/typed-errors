package info.jab.problems.problem2;

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

public class LatencyProblem01Test {

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

    private void loadStubs() {
        wireMockServer.stubFor(
            get(urlEqualTo("/greek"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200).withBodyFile("greek.json"))
        );

        wireMockServer.stubFor(
            get(urlEqualTo("/roman"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200).withBodyFile("roman.json"))
        );

        wireMockServer.stubFor(
            get(urlEqualTo("/nordic"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200).withBodyFile("nordic.json"))
        );
    }

    @Test
    public void should_solve_problem1() {
        //Given
        int timeout = 2;
        List<String> listOfGods = List.of("http://localhost:8090/greek", "http://localhost:8090/roman", "http://localhost:8090/nordic");
        BigInteger expectedResult = new BigInteger("78179288397447443426");
        loadStubs();

        //When
        LatencyProblem01 problem = new LatencyProblem01(listOfGods, timeout);
        var result = problem.javaStreamSolution();

        //Then
        assertThat(result).isEqualTo(expectedResult);
    }
}
