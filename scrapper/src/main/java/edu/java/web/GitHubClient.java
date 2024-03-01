package edu.java.web;

import edu.java.responseDTO.GitHubResponse;
import edu.java.util.ClientErrorCode;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

// We need to know owner and repository name
@Slf4j
public class GitHubClient {
    private final WebClient webClient;

    @Autowired
    ClientErrorCode errorCode;

    @SuppressWarnings("ParameterName")
    public GitHubClient(WebClient.Builder webClientBuilder, String URL) {
        this.webClient = webClientBuilder.baseUrl(URL).build();
    }

    public void logRequest(char thread, String messageCode, String request) {
        switch (thread) {
            case 'e' -> log.error(
                String.format(
                    errorCode.getClientErrorCode(messageCode)
                        + " with https://api.github.com. In function "
                        + Thread.currentThread().getStackTrace()[2],
                        request));
            case 'i' -> log.info("Info request");
            case 'w' -> log.warn("Warn request");
            default -> log.error("Invalid thread argument");
        }
    }

    @SuppressWarnings("MultipleStringLiterals")
    public GitHubResponse getResponse(String owner, String repo) {
        try {
            return webClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .bodyToMono(GitHubResponse.class).block();
        } catch (WebClientResponseException.NotFound e) {
            logRequest('e', "EXIT_CODE_404",
                String.format("/repos/%s/%s", owner, repo));
        } catch (WebClientResponseException.BadRequest e) {
            logRequest('e', "EXIT_CODE_400",
                String.format("/repos/%s/%s", owner, repo)
            );
        } catch (WebClientResponseException e) {
            logRequest('e', "EXIT_CODE_1",
                String.format("/repos/%s/%s", owner, repo));
        }
        return new GitHubResponse("-1", OffsetDateTime.MIN);
    }
}
