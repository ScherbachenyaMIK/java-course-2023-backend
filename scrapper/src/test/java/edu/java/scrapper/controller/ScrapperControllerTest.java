package edu.java.scrapper.controller;

import edu.java.model.requestDTO.AddLinkRequest;
import edu.java.model.requestDTO.RemoveLinkRequest;
import edu.java.model.responseDTO.ApiErrorResponse;
import edu.java.model.responseDTO.LinkResponse;
import edu.java.model.responseDTO.ListLinksResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ScrapperControllerTest {

    private final WebClient webClient;

    @Autowired
    public ScrapperControllerTest(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @Test
    void addLink() throws URISyntaxException {
        Long id = 1L;
        URI url = new URI("https://api.stackexchange.com/2.2/questions" +
            "/78110387?order=desc&sort=activity&site=stackoverflow");
        AddLinkRequest addLinkRequest = new AddLinkRequest(url);
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode result = Objects.requireNonNull(webClient.post()
                .uri("/links")
                .header("Tg-Chat-Id", id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addLinkRequest)
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block())
            .getStatusCode();
        assertEquals(expected, result);
    }

    @Test
    void getAllLinks() throws URISyntaxException {
        long id = 1L;
        URI url = new URI("https://github.com/ScherbachenyaMIK/java-course-2023-backend");
        List<LinkResponse> links = new ArrayList<>();
        links.add(new LinkResponse(1L, url));
        ListLinksResponse result = webClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", Long.toString(id))
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .block();
        assertNotNull(result);
        assertEquals(links.size(), result.size());
        assertEquals(links.getFirst(), result.links().getFirst());
    }

    @Test
    void removeLink() throws URISyntaxException {
        Long id = 1L;
        URI url = new URI("https://api.stackexchange.com/2.2/questions" +
            "/78110387?order=desc&sort=activity&site=stackoverflow");
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(url);
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode result = Objects.requireNonNull(webClient.method(HttpMethod.DELETE)
                .uri("/links")
                .header("Tg-Chat-Id", id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(removeLinkRequest)
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block())
            .getStatusCode();
        assertEquals(expected, result);
    }

    @Test
    void registerChat() {
        Long id = 5L;
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode response = Objects.requireNonNull(webClient.post()
                .uri("/tg-chat/{id}", id)
                .header("id", id.toString())
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block())
            .getStatusCode();

        assertEquals(expected, response);
    }

    @Test
    void deleteChat() {
        Long id = 1L;
        HttpStatusCode expected = HttpStatus.OK;

        HttpStatusCode response = Objects.requireNonNull(webClient.delete()
                .uri("/tg-chat/{id}", id)
                .header("id", id.toString())
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block())
            .getStatusCode();

        assertEquals(expected, response);
    }

    @Test
    void addLinkBadRequest() {
        Long id = 1L;
        AddLinkRequest addLinkRequest = new AddLinkRequest(null);

        assertThrows(WebClientResponseException.BadRequest.class,
            () -> webClient.post()
            .uri("/links", id)
            .header("Tg-Chat-Id", id.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(addLinkRequest)
            .retrieve()
            .bodyToMono(ApiErrorResponse.class)
            .block());
    }

    @Test
    void removeLinkNotFound() throws URISyntaxException {
        Long id = 1L;
        URI url = new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(url);

        assertThrows(WebClientResponseException.NotFound.class,
            () -> webClient.method(HttpMethod.DELETE)
                .uri("/links", id)
                .header("Tg-Chat-Id", id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(removeLinkRequest)
                .retrieve()
                .bodyToMono(ApiErrorResponse.class)
                .block());
    }

    @Test
    void removeLinkBadRequest() {
        Long id = 1L;
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(null);

        assertThrows(WebClientResponseException.BadRequest.class,
            () -> webClient.method(HttpMethod.DELETE)
                .uri("/links", id)
                .header("Tg-Chat-Id", id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(removeLinkRequest)
                .retrieve()
                .bodyToMono(ApiErrorResponse.class)
                .block());
    }

    @Test
    void registerChatAlreadyRegisteredUser() {
        Long id = 1L;
        ApiErrorResponse expected =
            new ApiErrorResponse(
                "Пользователь уже зарегистрирован",
                "CREATED",
                "edu.java.exception.UserAlreadyRegisteredException",
                "User already registered",
                null
        );

        ApiErrorResponse response = webClient.post()
                .uri("/tg-chat/{id}", id)
                .header("id", id.toString())
                .retrieve()
                .bodyToMono(ApiErrorResponse.class)
                .block();

        assertNotNull(response);
        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("stacktrace")
            .isEqualTo(expected);
    }

    @Test
    void deleteChatNotRegisteredUser() {
        Long id = 5L;

        assertThrows(WebClientResponseException.NotFound.class,
            () -> webClient.delete()
                .uri("/tg-chat/{id}", id)
                .header("id", id.toString())
                .retrieve()
                .bodyToMono(ApiErrorResponse.class)
                .block());
    }
}
