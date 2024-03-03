package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
public class ListCommand implements FunctionalCommand {
    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Displays the list of tracked website URLs. "
            + "Upon entering this command, the bot shows a list of all the "
            + "URLs of websites currently being tracked.";
    }

    @Override
    public String shortDescription() {
        return "Prints all tracked links";
    }

    @Override
    public SendMessage handle(Update update) {
        // Writing of list of tracking links
        return new SendMessage(update.message().chat().id(),
            """
                  Command is recognized

                  You enter command /list!
                  """);
    }
}
