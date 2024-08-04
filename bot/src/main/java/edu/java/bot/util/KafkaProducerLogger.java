package edu.java.bot.util;

import edu.java.bot.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KafkaProducerLogger {
    private final ApplicationConfig applicationConfig;

    public void logRequest(String message) {
        log.debug("Message pushed into queue");
        log.debug("Topic: {}", applicationConfig.kafkaConfiguration().dlqTopicName());
        log.debug("Message body: {}", message);
    }
}
