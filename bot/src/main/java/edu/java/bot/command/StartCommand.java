package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.web.ScrapperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(value = 0)
public class StartCommand implements FunctionalCommand {
    @Autowired
    private ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Initiates the bots operation. "
            + "Upon entering this command, the bot starts its operation "
            + "and is ready to accept further instructions.";
    }

    @Override
    public String shortDescription() {
        return "Starts bot";
    }

    @Override
    public SendMessage handle(Update update) {
        // User logging
        if (scrapperClient.postTgChatId(update.message().chat().id()) != HttpStatus.OK) {
            return new SendMessage(update.message().chat().id(),
                """
                      You already register!

                      Use the command /track to add the websites you're interested in.
                      """);
        }
        return new SendMessage(update.message().chat().id(),
            """
                  Bot launched!

                  Use the command /track to add the websites you're interested in.
                  """);
    }
}
