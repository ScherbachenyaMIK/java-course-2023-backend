package edu.java.bot.controller;

import edu.java.bot.model.requestDTO.LinkUpdateRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BotControllerTest {

    private final WebClient webClient;

    @Autowired
    public BotControllerTest(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8090").build();
    }

    @Test
    public void testProcessUpdate() throws URISyntaxException {
        LinkUpdateRequest request = new LinkUpdateRequest(1L,
            new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            "Test description", new ArrayList<>());

        ResponseEntity<Void> responseEntity = webClient.post()
            .uri("/updates")
            .bodyValue(request)
            .retrieve()
            .toEntity(Void.class)
            .block();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testProcessUpdateBadRequest() {
        LinkUpdateRequest request = new LinkUpdateRequest(1L,
            null,
            "Test description", new ArrayList<>());

        assertThrows(WebClientResponseException.BadRequest.class,
            () -> webClient.post()
            .uri("/updates")
            .bodyValue(request)
            .retrieve()
            .toEntity(Void.class)
            .block());
    }

    @Test
    public void testProcessUpdateInternalServerError() throws URISyntaxException {
        String request = new LinkUpdateRequest(1L,
            new URI("https://github.com/ScherbachenyaMIK/java-course-2023-backend"),
            "Test description", new ArrayList<>()).toString();

        assertThrows(WebClientResponseException.InternalServerError.class,
            () -> webClient.post()
                .uri("/updates")
                .bodyValue(request)
                .retrieve()
                .toEntity(Void.class)
                .block());
    }
}

