package edu.java.scrapper.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.responseDTO.BotRequest;
import edu.java.web.BotHttpClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BotHttpClientTest {

    BotHttpClient botHttpClient = new BotHttpClient(WebClient.builder(), "http://localhost:8090");
    private static final int PORT = 8090;
    private static final String HOST = "localhost";
    private static final Long ID = 1L;
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
    void sendUpdate() throws URISyntaxException {
        stubFor(post(urlEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(200)
                .withStatusMessage("OK")
                ));
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        BotRequest request = new BotRequest(
            1L,
            new URI("https://github.com/ScherbachenyaMIK/java-course-2023-backend"),
            "New commit pushed",
            ids
        );
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode result = botHttpClient.sendUpdate(request);

        assertEquals(expected, result);
    }

    @Test
    void sendUpdateBadRequest() {
        stubFor(post(urlEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(200)
                .withStatusMessage("OK")
            ));
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode result = botHttpClient.sendUpdate(new BotRequest(
            1L,
            null,
            "New commit pushed",
            null
        ));

        assertEquals(expected, result);
    }
}
