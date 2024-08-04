package edu.java.bot.util;

import edu.java.bot.web.BotQueueProducer_dlq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListenerErrorHandler implements KafkaListenerErrorHandler {
    @Autowired
    BotQueueProducer_dlq botQueueProducerDlq;

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        logRequest(message, exception);
        message.getPayload();
        botQueueProducerDlq.sendUpdate(message.getPayload().toString());
        return null;
    }

    private void logRequest(Message<?> message, ListenerExecutionFailedException exception) {
        log.error("Failed to process message from kafka topic");
        log.error("processing throw exception: {}", exception.getClass());
        log.error("description: {}", exception.getMessage());
        log.error("message contents: {}", message.getPayload());
    }
}
