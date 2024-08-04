package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.requestDTO.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    @Autowired
    CustomTelegramBot telegramBot;

    public void sendMessage(LinkUpdateRequest linkUpdateRequest) {
        for (Long chatId : linkUpdateRequest.tgChatIds()) {
            telegramBot.execute(new SendMessage(
                chatId,
                linkUpdateRequest.url().toString()
                    + "\n"
                    + linkUpdateRequest.description()
            ));
        }
    }
}
