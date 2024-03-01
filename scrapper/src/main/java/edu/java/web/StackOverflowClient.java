package edu.java.web;

import edu.java.responseDTO.StackOverflowResponse;
import edu.java.util.ClientErrorCode;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

// We need to know only question id
@Slf4j
public class StackOverflowClient {
    private final WebClient webClient;

    @Autowired
    ClientErrorCode errorCode;

    @SuppressWarnings("ParameterName")
    public StackOverflowClient(WebClient.Builder webClientBuilder, String URL) {
        this.webClient = webClientBuilder.baseUrl(URL).build();
    }

    public void logRequest(char thread, String messageCode, String id) {
        switch (thread) {
            case 'e' -> log.error(
                String.format(
                    errorCode.getClientErrorCode(messageCode)
                        + " with https://api.stackexchange.com/2.2. In function "
                        + Thread.currentThread().getStackTrace()[2],
                    id));
            case 'i' -> log.info("Info request");
            case 'w' -> log.warn("Warn request");
            default -> log.error("Invalid thread argument");
        }
    }

    @SuppressWarnings("MultipleStringLiterals")
    public StackOverflowResponse getResponse(String id) {
        try {
        return webClient.get()
            .uri("/questions/{id}?order=desc&sort=activity&site=stackoverflow", id)
            .retrieve()
            .bodyToMono(StackOverflowResponse.class)
            .block();
        } catch (
            WebClientResponseException.NotFound e) {
            logRequest('e', "EXIT_CODE_404",
                String.format("/questions/%s", id));
        } catch (
            WebClientResponseException.BadRequest e) {
            logRequest('e', "EXIT_CODE_400",
                String.format("/questions/%s", id));
        } catch (WebClientResponseException e) {
            logRequest('e', "EXIT_CODE_1",
                String.format("/questions/%s", id));
        }
        return new StackOverflowResponse("-1", OffsetDateTime.MIN);
    }
}
