package edu.java.bot.configuration;

import edu.java.bot.web.ScrapperClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {

    @Bean
    public ScrapperClient scrapperClient(WebClient.Builder webClientBuilder) {
        return new ScrapperClient(webClientBuilder, "http://localhost:8080");
    }
}
