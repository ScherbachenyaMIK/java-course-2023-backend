package edu.java.web;

import edu.java.util.UserResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public abstract class WebSiteClient {
    private final WebClient webClient;
    protected final List<String> pathList = new ArrayList<>();

    @SuppressWarnings("ParameterName")
    @Autowired(required = false)
    public WebSiteClient(WebClient.Builder webClientBuilder, String URL) {
        this.webClient = webClientBuilder.baseUrl(URL).build();
    }

    public List<UserResponse> getResponse() {
        List<UserResponse> responses = new ArrayList<>();
        for (var path : pathList) {
            Pair<String, String> pair = fetchData(getMono(path));
            responses.add(new UserResponse(parseDate(pair.getLeft()), pair.getRight()));
        }
        return responses;
    }

    protected Mono<String> getMono(String path) {
        return webClient.get()
            .uri(path)
            .retrieve()
            .bodyToMono(String.class);
    }

    protected abstract Pair<String, String> fetchData(Mono<String> mono);

    protected abstract OffsetDateTime parseDate(String date);

    //protected abstract takePaths(); //takes paths from db

    @SuppressWarnings("ParameterName")
    public void addURL(String URL) {
        pathList.add(URL);
    }
}
