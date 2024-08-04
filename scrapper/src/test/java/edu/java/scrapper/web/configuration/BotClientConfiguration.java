package edu.java.scrapper.web.configuration;

import edu.java.configuration.ApplicationConfig;
import edu.java.responseDTO.BotRequest;
import edu.java.util.KafkaProducerLogger;
import edu.java.web.BotHttpClient;
import edu.java.web.ScrapperQueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BotClientConfiguration {
    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    KafkaTemplate<String, BotRequest> kafkaTemplate;

    @Autowired
    KafkaProducerLogger kafkaProducerLogger;

    @Bean
    public BotHttpClient botHttpClient() {
        return new BotHttpClient(
            WebClient.builder(),
            "http://localhost:8090"
        );
    }

    @Bean
    public ScrapperQueueProducer scrapperQueueProducer() {
        return new ScrapperQueueProducer(kafkaTemplate, applicationConfig, kafkaProducerLogger);
    }
}
