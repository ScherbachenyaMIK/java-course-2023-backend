package edu.java.bot.configuration;

import edu.java.bot.util.KafkaProducerLogger;
import edu.java.bot.web.BotQueueProducer_dlq;
import edu.java.bot.web.ScrapperClient;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationProperties(prefix = "scrapper")
public class ClientConfiguration {
    @Setter
    private String baseUrl;

    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public ScrapperClient scrapperClient(WebClient.Builder webClientBuilder) {
        return new ScrapperClient(webClientBuilder, baseUrl);
    }

    @Bean
    public KafkaProducerLogger kafkaProducerLogger() {
        return new KafkaProducerLogger(applicationConfig);
    }

    @SuppressWarnings("MethodName")
    @Bean
    public BotQueueProducer_dlq BotQueueProducer_dlq(
    ) {
        return new BotQueueProducer_dlq(kafkaTemplate, applicationConfig, kafkaProducerLogger());
    }
}
