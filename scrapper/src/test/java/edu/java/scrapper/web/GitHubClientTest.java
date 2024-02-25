package edu.java.scrapper.web;

import edu.java.web.GitHubClient;
import edu.java.web.WebSiteClient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitHubClientTest {
    private WebClient.Builder webClientBuilder;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    public void setUp() {
        webClientBuilder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl("https://api.github.com")).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(mock(WebClient.class));
        when(webClientBuilder.baseUrl("https://api.github.com").build()).thenReturn(mock(WebClient.class));
        when(webClientBuilder.build()).thenReturn(mock(WebClient.class));
        when(webClientBuilder.build().get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/repos/owner/repo")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void testFetchData() {
        GitHubClient gitHubClient = new GitHubClient(webClientBuilder);
        String json = "{\"updated_at\": \"2022-02-24T10:30:00Z\", \"id\": \"123456\"}";

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(json));
        Pair<String, String> result = Pair.of("Error!", "Error!");

        try {
            Method getMono = WebSiteClient.class
                .getDeclaredMethod("getMono", String.class);
            getMono.setAccessible(true);
            Method fetchData = GitHubClient.class
                .getDeclaredMethod("fetchData", Mono.class);
            fetchData.setAccessible(true);

            result =
                (Pair<String, String>) fetchData.invoke(
                    gitHubClient,
                    getMono.invoke(gitHubClient, "/repos/owner/repo")
                );
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertEquals("2022-02-24T10:30:00Z", result.getLeft());
        assertEquals("123456", result.getRight());
    }

    @Test
    public void testParseDate() {
        GitHubClient gitHubClient = new GitHubClient(webClientBuilder);
        OffsetDateTime result = OffsetDateTime.now();
        try {
            Method parseDate = GitHubClient.class
                .getDeclaredMethod("parseDate", String.class);
            parseDate.setAccessible(true);
            result =
                (OffsetDateTime) parseDate.invoke(gitHubClient,
                    "2022-02-24T10:30:00Z");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        assertEquals(
            OffsetDateTime.of(
                2022,
                2,
                24,
                10,
                30,
                0,
                0,
                ZoneOffset.UTC),
            result);
    }
}
