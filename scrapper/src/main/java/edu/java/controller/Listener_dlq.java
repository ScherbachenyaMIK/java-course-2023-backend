package edu.java.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@SuppressWarnings("TypeName")
@Service
@Slf4j
public class Listener_dlq {
    @RetryableTopic(attempts = "1")
    @KafkaListener(topics = "${app.kafka-configuration.dlq-topic-name}")
    public void listen(String deadMessage) {
        log.debug("Message was not processed and was sent to the dead letter queue");
        log.debug("Message: {}", deadMessage);
    }
}
