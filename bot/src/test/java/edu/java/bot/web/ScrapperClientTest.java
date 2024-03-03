package edu.java.bot.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.model.requestDTO.LinkRequest;
import edu.java.bot.model.responseDTO.LinkResponse;
import edu.java.bot.model.responseDTO.ListLinksResponse;
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
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ScrapperClientTest {

    ScrapperClient scrapperClient = new ScrapperClient(WebClient.builder(), "http://localhost:8080");
    private static final int PORT = 8080;
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
    void getLinksTest() {
        stubFor(get(urlEqualTo("/links"))
            .willReturn(aResponse()
                .withStatus(200)
                .withStatusMessage("OK")
                .withHeader("Content-Type", "application/json")
                .withBody("{\n" +
                        "  \"links\": [\n" +
                        "    {\n" +
                        "      \"id\": 1,\n" +
                        "      \"url\": \"https://github.com/ScherbachenyaMIK/java-course-2023-backend\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"size\": 1\n" +
                        "}"
                    )));
        List<LinkResponse> trackedLinks = new ArrayList<>();
        try {
            trackedLinks.add(new LinkResponse(
                1L,
                new URI("https://github.com/ScherbachenyaMIK/java-course-2023-backend")));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        ListLinksResponse expected = new ListLinksResponse(trackedLinks, trackedLinks.size());

        ListLinksResponse response = scrapperClient.getLinks(1L);

        assertEquals(expected, response);
    }

    @Test
    void postTgChatIdTest() {
        stubFor(post(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withStatusMessage("OK")
                ));
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode result = scrapperClient.postTgChatId(1L);

        assertEquals(expected, result);
    }

    @Test
    void deleteTgChatIdTest() {
        stubFor(delete(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withStatusMessage("OK")
            ));
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode result = scrapperClient.deleteTgChatId(1L);

        assertEquals(expected, result);
    }

    @Test
    void postLinksTest() throws URISyntaxException {
        stubFor(post(urlEqualTo("/links"))
            .willReturn(aResponse()
                .withStatus(200)
                .withStatusMessage("OK")
            ));
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode result = scrapperClient.postLinks(1L,
            new LinkRequest(new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend")));

        assertEquals(expected, result);
    }

    @Test
    void deleteLinksTest() throws URISyntaxException {
        stubFor(delete(urlEqualTo("/links"))
            .willReturn(aResponse()
                .withStatus(200)
                .withStatusMessage("OK")
            ));
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode result = scrapperClient.deleteLinks(1L,
            new LinkRequest(new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend")));

        assertEquals(expected, result);
    }
}
