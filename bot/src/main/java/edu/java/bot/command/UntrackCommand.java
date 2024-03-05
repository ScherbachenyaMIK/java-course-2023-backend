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
@Order(value = 4)
public class UntrackCommand implements FunctionalCommand {
    @Autowired
    private CommandErrorCode commandErrorCode;

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Removes a website from the list of tracked websites. "
            + "Upon entering this command, the bot allows you to remove a website "
            + "from the list of tracked websites. You will need to specify the URL "
            + "of the website you want to remove from the list. Example usage: /untrack [URL].";
    }

    @Override
    public String shortDescription() {
        return "Remove link from track list";
    }

    @Override
    public SendMessage handle(Update update) {
        // End tracking link
        return new SendMessage(update.message().chat().id(),
            """
                  Command is recognized

                  You enter command /untrack!
                  """);
    }

    @SuppressWarnings("MultipleStringLiterals")
    @Override
    public String supports(Update update) {
        // Getting of part
        String[] parts = update.message().text().split("\\s+");
        // Checking for empty request
        if (parts.length == 1 && parts[0].equals(command())) {
            return "EXIT_CODE_4";
        } else if (parts.length == 2 && parts[0].equals(command())) {
            // Checking for invalid request
            try {
                URI uri = new URI(parts[1]);
                // Checking for invalid URL
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
        if (exitCode.equals("EXIT_CODE_4")) {
            message += new ListCommand().handle(update)
                                        .getParameters()
                                        .get("text")
                                        .toString();
            return new SendMessage(update.message().chat().id(),
                message);
        }
        return new SendMessage(update.message().chat().id(), message);
    }
}
