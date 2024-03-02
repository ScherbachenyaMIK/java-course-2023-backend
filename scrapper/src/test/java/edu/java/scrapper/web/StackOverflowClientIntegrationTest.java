package edu.java.scrapper.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.responseDTO.StackOverflowResponse;
import edu.java.web.StackOverflowClient;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

@SpringBootTest
public class StackOverflowClientIntegrationTest {
    private final StackOverflowClient stackOverflowClient;
    private static final int PORT = 8089;
    private static final String HOST = "localhost";
    private static final String ID = "123456";
    private WireMockServer wireMockServer;

    @Autowired
    public StackOverflowClientIntegrationTest(StackOverflowClient stackOverflowClient) {
        this.stackOverflowClient = stackOverflowClient;
    }

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
        WireMock.configureFor(HOST, PORT);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testGetResponse() {
        // Making of stubs for request to StackOverflow API
        stubFor(get(urlEqualTo(String.format("/questions/%s?order=desc&sort=activity&site=stackoverflow", ID)))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(String.format("{\"items\": [{\"last_activity_date\": 1645698600, \"question_id\": %s}]}",
                    ID))));

        // Making of object GitHubClient with URL our WireMock server
        StackOverflowClient stackOverflowClient = new
            StackOverflowClient(WebClient.builder(), "http://" + HOST + ":" + PORT);

        StackOverflowResponse response = stackOverflowClient.getResponse(ID);

        // Checking for results
        assertEquals(OffsetDateTime.of(
                2022,
                2,
                24,
                10,
                30,
                0,
                0,
                ZoneOffset.UTC),
            response.lastActivityDate());
        assertEquals("123456",
            response.questionId());
    }

    @Test
    public void testGetResponseCrash() {
        StackOverflowResponse response = stackOverflowClient.getResponse(ID + 'a');

        // Checking for results
        Assertions.assertEquals("-1", response.questionId());
        Assertions.assertEquals(OffsetDateTime.MIN, response.lastActivityDate());
    }
}
