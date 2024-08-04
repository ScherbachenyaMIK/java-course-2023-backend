package edu.java.web;

import edu.java.configuration.ApplicationConfig;
import edu.java.responseDTO.BotRequest;
import edu.java.util.KafkaProducerLogger;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class ScrapperQueueProducer implements BotClient {

    @NotNull
    private final KafkaTemplate<String, BotRequest> kafkaTemplate;

    @NotNull
    private final ApplicationConfig applicationConfig;

    @NotNull
    private final KafkaProducerLogger kafkaProducerLogger;

    public HttpStatusCode sendUpdate(BotRequest botRequest) {
        kafkaProducerLogger.logRequest(botRequest);
        kafkaTemplate.send(applicationConfig.kafkaConfiguration().topicName(), botRequest);
        return HttpStatus.OK;
    }
}
