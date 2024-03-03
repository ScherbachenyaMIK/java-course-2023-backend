package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface Command {
    String command();

    SendMessage handle(Update update);

    default String supports(Update update) {
        return "EXIT_CODE_0";
    }

    default SendMessage handleNotSupports(Update update, String exitCode) {
        return new SendMessage(update.message().chat().id(),
            "Command is not supported");
    }
}
