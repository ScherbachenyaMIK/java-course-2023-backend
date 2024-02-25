package edu.java.scrapper.web;

import edu.java.web.StackOverflowClient;
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

public class StackOverflowClientTest {
    private WebClient.Builder webClientBuilder;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    public void setUp() {
        webClientBuilder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl("https://api.stackexchange.com/2.3")).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(mock(WebClient.class));
        when(webClientBuilder.baseUrl("https://api.stackexchange.com/2.3").build()).thenReturn(mock(WebClient.class));
        when(webClientBuilder.build()).thenReturn(mock(WebClient.class));
        when(webClientBuilder.build().get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/questions/123456")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void testFetchData() {
        StackOverflowClient stackOverflowClient = new StackOverflowClient(webClientBuilder);
        String json = "{\"items\": [{\"last_activity_date\": 1645698600, \"question_id\": 123456}]}";

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(json));
        Pair<String, String> result = Pair.of("Error!", "Error!");

        try {
            Method getMono = WebSiteClient.class
                .getDeclaredMethod("getMono", String.class);
            getMono.setAccessible(true);
            Method fetchData = StackOverflowClient.class
                .getDeclaredMethod("fetchData", Mono.class);
            fetchData.setAccessible(true);

            result =
                (Pair<String, String>) fetchData.invoke(
                    stackOverflowClient,
                    getMono.invoke(stackOverflowClient, "/questions/123456")
                );
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertEquals("1645698600", result.getLeft());
        assertEquals("123456", result.getRight());
    }

    @Test
    public void testParseDate() {
        StackOverflowClient stackOverflowClient = new StackOverflowClient(webClientBuilder);
        OffsetDateTime result = OffsetDateTime.now();
        try {
            Method parseDate = StackOverflowClient.class
                .getDeclaredMethod("parseDate", String.class);
            parseDate.setAccessible(true);
            result =
                (OffsetDateTime) parseDate.invoke(stackOverflowClient,
                    "1645698600");
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
