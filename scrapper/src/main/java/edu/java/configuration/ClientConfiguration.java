package edu.java.configuration;

import edu.java.responseDTO.BotRequest;
import edu.java.util.KafkaProducerLogger;
import edu.java.web.BotClient;
import edu.java.web.BotHttpClient;
import edu.java.web.GitHubClient;
import edu.java.web.ScrapperQueueProducer;
import edu.java.web.StackOverflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {
    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    KafkaTemplate<String, BotRequest> kafkaTemplate;

    @Bean
    public GitHubClient gitHubClient(WebClient.Builder webClientBuilder) {
        return new GitHubClient(webClientBuilder, "https://api.github.com");
    }

    @Bean
    public StackOverflowClient stackOverflowClient(WebClient.Builder webClientBuilder) {
        return new StackOverflowClient(webClientBuilder, "https://api.stackexchange.com/2.2");
    }

    @Bean
    public KafkaProducerLogger kafkaProducerLogger() {
        return new KafkaProducerLogger(applicationConfig);
    }

    @Bean
    public BotClient botClient(WebClient.Builder webClientBuilder) {
        if (applicationConfig.useQueue()) {
            return new ScrapperQueueProducer(kafkaTemplate, applicationConfig, kafkaProducerLogger());
        } else {
            return new BotHttpClient(webClientBuilder, "http://localhost:8090");
        }
    }
}
