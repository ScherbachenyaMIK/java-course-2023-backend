package edu.java.bot.service;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler {
    private static List<Command> commandList;

    @Autowired
    public void setCommandList(CommandList commandList) {
        MessageHandler.commandList = commandList.getCommandList();
    }

    public static Command handleMessage(Message message) {
        String text = message.text();

        // Logic of handling
        if (text != null && text.startsWith("/")) {
            for (var command : commandList) {
                if (text.startsWith(command.command())) {
                    return command;
                }
            }
        }
        return null;
    }
}
