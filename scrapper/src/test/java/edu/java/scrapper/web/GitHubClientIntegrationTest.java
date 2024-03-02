package edu.java.scrapper.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.responseDTO.GitHubResponse;
import edu.java.web.GitHubClient;
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

@SpringBootTest
public class GitHubClientIntegrationTest {
    private final GitHubClient gitHubClient;
    private static final int PORT = 8089;
    private static final String HOST = "localhost";
    private static final String OWNER = "owner";
    private static final String REPO = "repo";
    private WireMockServer wireMockServer;

    @Autowired
    public GitHubClientIntegrationTest(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
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
        // Making of stubs for request to GitHub API
        stubFor(get(urlEqualTo(String.format("/repos/%s/%s", OWNER, REPO)))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"updated_at\": \"2022-02-24T10:30:00Z\", \"id\": \"123456\"}")));

        // Making of object GitHubClient with URL our WireMock server
        GitHubClient gitHubClient = new GitHubClient(WebClient.builder(), "http://" + HOST + ":" + PORT);

        GitHubResponse response = gitHubClient.getResponse(OWNER, REPO);

        // Checking for results
        Assertions.assertEquals(OffsetDateTime.of(
            2022,
            2,
            24,
            10,
            30,
            0,
            0,
            ZoneOffset.UTC), response.updatedAt());
        Assertions.assertEquals("123456", response.id());
    }

    @Test
    public void testGetResponseCrash() {
        GitHubResponse response = gitHubClient.getResponse(OWNER, REPO);

        // Checking for results
        Assertions.assertEquals("-1", response.id());
        Assertions.assertEquals(OffsetDateTime.MIN, response.updatedAt());
    }
}
