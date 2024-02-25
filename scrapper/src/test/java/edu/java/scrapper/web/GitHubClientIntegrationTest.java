package edu.java.scrapper.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.util.UserResponse;
import edu.java.web.GitHubClient;
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

public class GitHubClientIntegrationTest {
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
        // Making of stubs for request to GitHub API
        stubFor(get(urlEqualTo("/repos/owner/repo"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"updated_at\": \"2022-02-24T10:30:00Z\", \"id\": \"123456\"}")));
        stubFor(get(urlEqualTo("/repos/owner/another_repo"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"updated_at\": \"2020-05-05T11:24:53Z\", \"id\": \"234567\"}")));

        // Making of object GitHubClient with URL our WireMock server
        GitHubClient gitHubClient = new GitHubClient(WebClient.builder());

        // Adding of URLs to client
        gitHubClient.addURL("/repos/owner/repo");
        gitHubClient.addURL("/repos/owner/another_repo");

        // Getting of private field webClient
        Field webClient;
        try {
            webClient = WebSiteClient.class.getDeclaredField("webClient");
        webClient.setAccessible(true);
        webClient.set(gitHubClient,
            WebClient.builder().baseUrl("http://" + HOST + ":" + PORT).build());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        List <UserResponse> responses = gitHubClient.getResponse();

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
