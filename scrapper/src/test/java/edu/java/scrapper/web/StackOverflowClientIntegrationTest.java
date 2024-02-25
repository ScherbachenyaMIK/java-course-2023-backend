package edu.java.scrapper.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.util.UserResponse;
import edu.java.web.StackOverflowClient;
import edu.java.web.WebSiteClient;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

public class StackOverflowClientIntegrationTest {
    private static final int PORT = 8089;
    private static final String HOST = "localhost";
    private WireMockServer wireMockServer;

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
        stubFor(get(urlEqualTo("/questions/123456?order=desc&sort=activity&site=stackoverflow"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\": [{\"last_activity_date\": 1645698600, \"question_id\": 123456}]}")));
        stubFor(get(urlEqualTo("/questions/234567?order=desc&sort=activity&site=stackoverflow"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\": [{\"last_activity_date\": 1588677893 , \"question_id\": 234567}]}")));

        // Making of object StackOverflowClient with URL our WireMock server
        StackOverflowClient stackOverflowClient = new StackOverflowClient(WebClient.builder());

        // Adding of URLs to client
        stackOverflowClient.addURL("/questions/123456");
        stackOverflowClient.addURL("/questions/234567");

        // Getting of private field webClient
        Field webClient;
        try {
            webClient = WebSiteClient.class.getDeclaredField("webClient");
            webClient.setAccessible(true);
            webClient.set(stackOverflowClient,
                WebClient.builder().baseUrl("http://" + HOST + ":" + PORT).build());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        List <UserResponse> responses = stackOverflowClient.getResponse();

        // Checking for results
        assertEquals(2, responses.size());
        assertEquals(OffsetDateTime.of(
                2022,
                2,
                24,
                10,
                30,
                0,
                0,
                ZoneOffset.UTC),
            responses.get(0).getTime());
        assertEquals("123456",
            responses.get(0).getIdentifier());
        assertEquals(OffsetDateTime.of(
                2020,
                5,
                5,
                11,
                24,
                53,
                0,
                ZoneOffset.UTC),
            responses.get(1).getTime());
        assertEquals("234567",
            responses.get(1).getIdentifier());
    }
}
