package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("DefaultAnnotationParam")
@Order(Ordered.LOWEST_PRECEDENCE)
public class UnknownCommand implements BaseCommand {
    @Override
    public String command() {
        return "/";
    }

    @Override
    public SendMessage handle(Update update) {
        // End tracking link
        return new SendMessage(update.message().chat().id(),
            """
                  Unknown command entered

                  If you want to see a list of commands, enter /help.
                  """);
    }
}
