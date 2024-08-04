package edu.java.util;

import edu.java.configuration.ApplicationConfig;
import edu.java.responseDTO.BotRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KafkaProducerLogger {
    private final ApplicationConfig applicationConfig;

    public void logRequest(BotRequest botRequest) {
        log.debug("Message pushed into queue");
        log.debug("Topic: {}", applicationConfig.kafkaConfiguration().topicName());
        log.debug("Message body: {}", botRequest);
    }
}
