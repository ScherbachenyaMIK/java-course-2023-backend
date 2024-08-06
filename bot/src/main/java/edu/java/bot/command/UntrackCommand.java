package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.requestDTO.LinkRequest;
import edu.java.bot.util.CommandErrorCode;
import edu.java.bot.util.Link;
import edu.java.bot.web.ScrapperClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Order(value = 4)
public class UntrackCommand implements FunctionalCommand {
    @Autowired
    private CommandErrorCode commandErrorCode;
    @Autowired
    private ScrapperClient scrapperClient;
    @Autowired
    ListCommand listCommand;

    private final Counter counter;

    public UntrackCommand(Counter counter, ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
        this.counter = counter;
    }

    @Autowired
    public UntrackCommand(MeterRegistry meterRegistry) {
        counter = Counter
            .builder("Telegram_messages_handled_total")
            .description("Counts how many times the /untrack command has been processed")
            .tags("command", command())
            .register(meterRegistry);
    }


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
    @SuppressWarnings("MagicNumber")
    public SendMessage handle(Update update) {
        counter.increment();
        // End tracking link
        String url = update.message().text().substring(9);
        try {
            scrapperClient.deleteLinks(update.message().chat().id(), new LinkRequest(URI.create(url)));
        } catch (WebClientResponseException.BadRequest e) {
            return new SendMessage(update.message().chat().id(),
                "Sorry, this url is not supported now, you can add "
                    + "either github repository url or stackoverflow question url ");
        } catch (WebClientResponseException.NotFound e) {
            return new SendMessage(update.message().chat().id(),
                """
                      This url is not tracked yet
                      """);
        }
        return new SendMessage(update.message().chat().id(),
            """
                  The bot will no longer track this url
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
        counter.increment();
        // Taking message in accordance with exit code
        String message = commandErrorCode.getCommandErrorCode(exitCode);
        if (exitCode.equals("EXIT_CODE_4")) {
            message += listCommand.handle(update)
                                    .getParameters()
                                    .get("text")
                                    .toString();
            return new SendMessage(update.message().chat().id(),
                message);
        }
        return new SendMessage(update.message().chat().id(), message);
    }
}
