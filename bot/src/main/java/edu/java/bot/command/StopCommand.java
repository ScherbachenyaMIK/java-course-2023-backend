package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.web.ScrapperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 5)
public class StopCommand implements BaseCommand {
    @Autowired
    private ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "close";
    }

    @Override
    public SendMessage handle(Update update) {
        scrapperClient.deleteTgChatId(update.myChatMember().chat().id());
        return null;
    }
}
