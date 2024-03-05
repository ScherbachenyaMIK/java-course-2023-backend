package edu.java.bot.web;

import edu.java.bot.model.requestDTO.LinkRequest;
import edu.java.bot.model.responseDTO.ListLinksResponse;
import java.util.Objects;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

public class ScrapperClient {
    private final WebClient webClient;

    @SuppressWarnings("ParameterName")
    public ScrapperClient(WebClient.Builder webClientBuilder, String URL) {
        this.webClient = webClientBuilder.baseUrl(URL).build();
    }

    @SuppressWarnings("MultipleStringLiterals")
    public ListLinksResponse getLinks(Long id) {
        return webClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", Long.toString(id))
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    @SuppressWarnings("MultipleStringLiterals")
    public HttpStatusCode postTgChatId(Long id) {
        return Objects.requireNonNull(
                webClient.post()
                .uri("/tg-chat/{id}", id)
                .header("id", id.toString())
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block())
            .getStatusCode();
    }

    public HttpStatusCode deleteTgChatId(Long id) {
        return Objects.requireNonNull(
                webClient.delete()
                .uri("/tg-chat/{id}", id)
                .header("id", id.toString())
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block())
            .getStatusCode();
    }

    public HttpStatusCode postLinks(Long id, LinkRequest link) {
        return Objects.requireNonNull(
                    webClient.post()
                    .uri("/links")
                    .header("Tg-Chat-Id", id.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(link)
                    .retrieve()
                    .toEntity(ResponseEntity.class)
                    .block())
            .getStatusCode();
    }

    public HttpStatusCode deleteLinks(Long id, LinkRequest link) {
        return Objects.requireNonNull(
                webClient.method(HttpMethod.DELETE)
                    .uri("/links")
                    .header("Tg-Chat-Id", id.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(link)
                    .retrieve()
                    .toEntity(ResponseEntity.class)
                    .block())
            .getStatusCode();
    }
}
