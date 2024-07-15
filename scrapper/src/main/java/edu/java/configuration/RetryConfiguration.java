package edu.java.configuration;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Getter
@Setter
@EnableRetry
@Configuration
@ConfigurationProperties(prefix = "spring.retry-config")
public class RetryConfiguration {
    private List<Integer> codesForRetry;

    @Bean
    List<Integer> codesForRetry() {
        WebClientResponseException exception;
        return codesForRetry;
    }
}
