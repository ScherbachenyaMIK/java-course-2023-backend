package edu.java.scrapper.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.responseDTO.BotRequest;
import edu.java.web.BotClient;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class RetryTest {
    @Autowired
    BotClient botClient;
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
    void botClientRetryTest() {
        stubFor(post(urlEqualTo("/updates"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withStatusMessage("Internal Server Error")
                ));
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        BotRequest request = new BotRequest(
                1L,
                URI.create("https://github.com/ScherbachenyaMIK/java-course-2023-backend"),
                "New commit pushed",
                ids
        );

        assertThatThrownBy(() -> botClient.sendUpdate(request))
                .isInstanceOf(WebClientResponseException.InternalServerError.class);

        verify(5, postRequestedFor(urlEqualTo("/updates")));
    }
}
