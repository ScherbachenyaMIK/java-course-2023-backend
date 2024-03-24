package edu.java.scrapper.controller;

import edu.java.DB.DTO.LinkDTO;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.exception.UserAlreadyRegisteredException;
import edu.java.model.requestDTO.AddLinkRequest;
import edu.java.model.requestDTO.RemoveLinkRequest;
import edu.java.model.responseDTO.ApiErrorResponse;
import edu.java.model.responseDTO.LinkResponse;
import edu.java.model.responseDTO.ListLinksResponse;
import edu.java.service.LinkService;
import edu.java.service.TgChatService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ScrapperControllerTest {

    private final WebClient webClient;

    @MockBean
    private LinkService linkService;

    @MockBean
    private TgChatService tgChatService;

    @Autowired
    public ScrapperControllerTest(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addGitHubLink() throws URISyntaxException {
        HttpStatusCode expectedStatusCode = HttpStatus.OK;
        Long tgChatId = 1L;
        URI url = new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        when(linkService.add(1L, url))
            .thenReturn(new LinkDTO(
            1L,
                url,
                null,
                null
                ));

        HttpStatusCode result = Objects.requireNonNull(
                webClient.post()
                    .uri("/links")
                    .header("Tg-Chat-Id", tgChatId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new AddLinkRequest(url))
                    .retrieve()
                    .toEntity(ResponseEntity.class)
                    .block())
            .getStatusCode();

        assertEquals(expectedStatusCode, result);
        verify(linkService).add(1L, url);
    }

    @Test
    void addStackOverflowLink() throws URISyntaxException {
        HttpStatusCode expectedStatusCode = HttpStatus.OK;
        Long tgChatId = 1L;
        URI url = new URI("https://api.stackexchange.com/2.2/questions"
            + "/123456?order=desc&sort=activity&site=stackoverflow");

        when(linkService.add(1L, url))
            .thenReturn(new LinkDTO(
                1L,
                url,
                null,
                null
            ));

        HttpStatusCode result = Objects.requireNonNull(
                webClient.post()
                    .uri("/links")
                    .header("Tg-Chat-Id", tgChatId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new AddLinkRequest(url))
                    .retrieve()
                    .toEntity(ResponseEntity.class)
                    .block())
            .getStatusCode();

        assertEquals(expectedStatusCode, result);
        verify(linkService).add(1L, url);
    }

    @Test
    void addLinkWhenUserNotRegistered() {
        HttpStatusCode expectedStatusCode = HttpStatus.BAD_REQUEST;
        Long tgChatId = 1L;
        URI url = URI.create("https://api.stackexchange.com/2.2/questions"
            + "/123456?order=desc&sort=activity&site=stackoverflow");
        when(linkService.add(1L, url))
            .thenThrow(NoSuchUserRegisteredException.class);

        assertThrows(WebClientResponseException.NotFound.class, () ->
                     webClient.post()
                    .uri("/links")
                    .header("Tg-Chat-Id", tgChatId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new AddLinkRequest(url))
                    .retrieve()
                    .bodyToMono(ApiErrorResponse.class)
                    .block());
        verify(linkService).add(1L, url);
    }

    @Test
    void listAllLinksForExistingChat() {
        List<LinkDTO> trackedLinks = new ArrayList<>();
        trackedLinks.add(new LinkDTO(
            1L,
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            null,
            null));
        trackedLinks.add(new LinkDTO(
            2L,
            URI.create("https://api.stackexchange.com/2.2/questions"
                + "/123456?order=desc&sort=activity&site=stackoverflow"),
            null,
            null));
        Long tgChatId = 1L;
        when(linkService.listAll(tgChatId))
            .thenReturn(trackedLinks);

        List<LinkResponse> expectedListResult = new ArrayList<>();
        expectedListResult.add(new LinkResponse(
            1L,
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend")));
        expectedListResult.add(new LinkResponse(
            2L,
            URI.create("https://api.stackexchange.com/2.2/questions"
                + "/123456?order=desc&sort=activity&site=stackoverflow")));
        ListLinksResponse expectedResult = new ListLinksResponse(expectedListResult, expectedListResult.size());

        ListLinksResponse result = webClient.get()
                    .uri("/links")
                    .header("Tg-Chat-Id", tgChatId.toString())
                    .retrieve()
                    .bodyToMono(ListLinksResponse.class)
                    .block();

        assertThat(result).isEqualTo(expectedResult);
        verify(linkService).listAll(1L);
    }

    @Test
    void listAllLinksForExistingChatWithNoLinks() {
        List<LinkDTO> trackedLinks = new ArrayList<>();
        Long tgChatId = 1L;
        when(linkService.listAll(tgChatId))
            .thenReturn(trackedLinks);

        List<LinkResponse> expectedListResult = new ArrayList<>();
        ListLinksResponse expectedResult = new ListLinksResponse(expectedListResult, expectedListResult.size());

        ListLinksResponse result = webClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", tgChatId.toString())
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .block();

        assertThat(result).isEqualTo(expectedResult);
        verify(linkService).listAll(1L);
    }

    @Test
    void listAllLinksForNonExistingChat() {
        Long tgChatId = 1L;
        when(linkService.listAll(tgChatId))
            .thenThrow(NoSuchUserRegisteredException.class);

        assertThrows(WebClientResponseException.NotFound.class, () -> webClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", tgChatId.toString())
            .retrieve()
            .bodyToMono(ApiErrorResponse.class)
            .block());
        verify(linkService).listAll(1L);
    }

    @Test
    void removeLinkForExistingChat() throws URISyntaxException {
        Long id = 1L;
        URI url = new URI("https://api.stackexchange.com/2.2/questions" +
            "/78110387?order=desc&sort=activity&site=stackoverflow");
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(url);
        HttpStatusCode expected = HttpStatus.OK;
        when(linkService.remove(id, url))
            .thenReturn(new LinkDTO(
                id,
                url,
                null,
                null
            ));

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
    void removeLinkForNonExistingChat() throws URISyntaxException {
        Long id = 1L;
        URI url = new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(url);
        when(linkService.remove(id, url))
            .thenThrow(NoSuchUserRegisteredException.class);

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
    void removeNonExistingLinkForExistingChat() throws URISyntaxException {
        Long id = 1L;
        URI url = new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(url);
        when(linkService.remove(id, url))
            .thenThrow(LinkNotFoundException.class);

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
    void registerChatForNonRegisteredUser() {
        Long id = 1L;
        HttpStatusCode expected = HttpStatus.OK;
        doNothing().when(tgChatService).register(id);

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
    void registerChatForAlreadyRegisteredUser() {
        Long id = 1L;
        HttpStatusCode expectedStatus = HttpStatus.CREATED;
        doThrow(new UserAlreadyRegisteredException()).when(tgChatService).register(id);

        HttpStatusCode resultStatus = Objects.requireNonNull(webClient.post()
                .uri("/tg-chat/{id}", id)
                .header("id", id.toString())
                .retrieve()
                .toEntity(Void.class)
                .block())
            .getStatusCode();

        assertEquals(expectedStatus, resultStatus);
    }

    @Test
    void deleteChatForRegisteredChat() {
        Long id = 1L;
        HttpStatus expectedStatus = HttpStatus.OK;
        doNothing().when(tgChatService).unregister(id);

        HttpStatusCode responseStatus = webClient.delete()
            .uri("/tg-chat/{id}", id)
            .header("id", id.toString())
            .retrieve()
            .toEntity(Void.class)
            .block()
            .getStatusCode();

        assertEquals(expectedStatus, responseStatus);
    }

    @Test
    void deleteChatForNotRegisteredUser() {
        Long id = 5L;
        doThrow(new NoSuchUserRegisteredException()).when(tgChatService).unregister(id);

        assertThrows(WebClientResponseException.NotFound.class,
            () -> webClient.delete()
                .uri("/tg-chat/{id}", id)
                .header("id", id.toString())
                .retrieve()
                .bodyToMono(ApiErrorResponse.class)
                .block());
    }
}
