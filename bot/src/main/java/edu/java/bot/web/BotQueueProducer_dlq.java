package edu.java.bot.web;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.util.KafkaProducerLogger;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;

@SuppressWarnings("TypeName")
@RequiredArgsConstructor
public class BotQueueProducer_dlq {

    @NotNull
    private final KafkaTemplate<String, String> kafkaTemplate;

    @NotNull
    private final ApplicationConfig applicationConfig;

    @NotNull
    private final KafkaProducerLogger kafkaProducerLogger;

    public void sendUpdate(String requestReply) {
        kafkaProducerLogger.logRequest(requestReply);
        kafkaTemplate.send(applicationConfig.kafkaConfiguration().dlqTopicName(), requestReply);
    }
}
