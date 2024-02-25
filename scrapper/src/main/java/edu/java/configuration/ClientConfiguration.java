package edu.java.configuration;

import edu.java.web.GitHubClient;
import edu.java.web.StackOverflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public ClientConfiguration(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Bean
    public GitHubClient gitHubClient() {
        return new GitHubClient(webClientBuilder);
    }

    @Bean
    public StackOverflowClient stackOverflowClient() {
        return new StackOverflowClient(webClientBuilder);
    }
}
