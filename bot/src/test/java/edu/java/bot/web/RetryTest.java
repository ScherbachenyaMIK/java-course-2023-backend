package edu.java.bot.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.model.requestDTO.LinkRequest;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class RetryTest {
    @Autowired
    ScrapperClient scrapperClient;
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
    void retryTest() {
        stubFor(delete(urlEqualTo("/links"))
            .willReturn(aResponse()
                .withStatus(500)
                .withStatusMessage("Internal Server Error")
            ));

        assertThatThrownBy(() -> scrapperClient.deleteLinks(1L,
            new LinkRequest(URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"))))
            .isInstanceOf(WebClientResponseException.InternalServerError.class);

        verify(5, deleteRequestedFor(urlEqualTo("/links")));
    }
}
