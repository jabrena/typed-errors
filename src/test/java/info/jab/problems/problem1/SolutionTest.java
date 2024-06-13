package info.jab.problems.problem1;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SolutionTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    private static void setup() {
        // Start WireMock server on a dynamic port
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        // Configure WireMock
        configureFor("localhost", wireMockServer.port());

        // Stub the response
        // @formatter:off
        stubFor(get(urlEqualTo("/"))
            .willReturn(aResponse()
            .withHeader("Content-Type", "text/html")
            .withBody("<html><body>Hello, World!</body></html>"))
        );
        // @formatter:on
    }

    @AfterAll
    public static void teardown() {
        // Stop WireMock server
        wireMockServer.stop();
    }

    // @formatter:off
    static Stream<ISolution> provideSolutions() {
        return Stream.of(
            new Solution1(), 
            new Solution2(), 
            new Solution3(), 
            new Solution4(),
            new Solution5(), 
            new Solution6(),
            new Solution7(),
            new Solution8(),
            new Solution9());
    }

    // @formatter:on

    @ParameterizedTest
    @MethodSource("provideSolutions")
    void should_work(ISolution solution) {
        //Given
        String address = "http://localhost:" + wireMockServer.port();

        //When
        var result = solution.extractHTML(address);

        //Then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo("<html><body>Hello, World!</body></html>");
    }

    @ParameterizedTest
    @MethodSource("provideSolutions")
    void should_not_work(ISolution solution) {
        //Given
        String address = "https://www.google999.com";

        //When
        var result = solution.extractHTML(address);

        //Then
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideSolutions")
    void should_not_work_with_bad_adddress(ISolution solution) {
        //Given
        String address = "https://www.localhost:80808.com";

        //When
        var result = solution.extractHTML(address);

        //Then
        assertThat(result).isEmpty();
    }
}
