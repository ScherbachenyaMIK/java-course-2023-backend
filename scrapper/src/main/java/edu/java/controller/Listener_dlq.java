package edu.java.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@SuppressWarnings("TypeName")
@Service
public class Listener_dlq {
    private final Logger log = LoggerFactory.getLogger(Listener_dlq.class);

    @RetryableTopic(attempts = "1")
    @KafkaListener(topics = "${app.kafka-configuration.dlq-topic-name}")
    public void listen(String deadMessage) {
        log.debug("Message was not processed and was sent to the dead letter queue");
        log.debug("Message: {}", deadMessage);
    }
}
