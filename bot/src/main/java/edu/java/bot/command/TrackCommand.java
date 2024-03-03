package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.util.CommandErrorCode;
import edu.java.bot.util.Link;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 3)
public class TrackCommand implements FunctionalCommand {
    @Autowired
    private CommandErrorCode commandErrorCode;

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "Adds a website with the specified URL to the list. "
            + "After the /track command, you need to specify the URL of the website "
            + "you want to add to the list of tracked websites. Example usage: /track [URL].";
    }

    @Override
    public String shortDescription() {
        return "Add link to track list";
    }

    @Override
    public SendMessage handle(Update update) {
        // Start tracking link
        return new SendMessage(update.message().chat().id(),
            """
                  Command is recognized

                  You enter command /track!
                  """);
    }

    @Override
    public String supports(Update update) {
        String[] parts = update.message().text().split("\\s+");
        if (parts.length == 1 && parts[0].equals(command())) {
            return "EXIT_CODE_2";
        } else if (parts.length == 2 && parts[0].equals(command())) {
            try {
                URI uri = new URI(parts[1]);
                Link.parse(uri);
                return "EXIT_CODE_0";
            } catch (URISyntaxException e) {
                return "EXIT_CODE_1";
            }
        }
        return "EXIT_CODE_3";
    }

    @Override
    public SendMessage handleNotSupports(Update update, String exitCode) {
        // Taking message in accordance with exit code
        String message = commandErrorCode.getCommandErrorCode(exitCode);
        return new SendMessage(update.message().chat().id(), message);
    }
}
