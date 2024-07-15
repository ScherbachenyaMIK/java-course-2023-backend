package edu.java.web;

import edu.java.annotation.LinearBackoffRetry;
import edu.java.responseDTO.BotRequest;
import java.util.Objects;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

public class BotClient {
    private final WebClient webClient;

    @SuppressWarnings("ParameterName")
    public BotClient(WebClient.Builder webClientBuilder, String URL) {
        this.webClient = webClientBuilder.baseUrl(URL).build();
    }

    @LinearBackoffRetry
    public HttpStatusCode sendUpdate(BotRequest botRequest) {
        return Objects.requireNonNull(
                webClient.post()
                    .uri("/updates")
                    .bodyValue(botRequest)
                    .retrieve()
                    .toEntity(ResponseEntity.class)
                    .block())
            .getStatusCode();
    }
}
