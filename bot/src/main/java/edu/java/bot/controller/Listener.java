package edu.java.bot.controller;

import edu.java.bot.model.requestDTO.LinkUpdateRequest;
import edu.java.bot.service.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
public class Listener {
    @Autowired
    MessageSender messageSender;

    @RetryableTopic(attempts = "1")
    @KafkaListener(topics = "${app.kafka-configuration.topic-name}", errorHandler = "listenerErrorHandler")
    public void listen(LinkUpdateRequest linkUpdateRequest) {
        messageSender.sendMessage(linkUpdateRequest);
    }
}
