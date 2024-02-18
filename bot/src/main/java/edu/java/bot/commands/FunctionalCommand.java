package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;

public interface FunctionalCommand extends Command {
    String description();

    String shortDescription();

    default BotCommand toApiCommand() {
        return new BotCommand(command().substring(1), shortDescription());
    }
}
