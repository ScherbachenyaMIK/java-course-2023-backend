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
@Order(value = 3)
public class TrackCommand implements FunctionalCommand {
    @Autowired
    private ScrapperClient scrapperClient;
    @Autowired
    private CommandErrorCode commandErrorCode;

    private final Counter counter;

    public TrackCommand(Counter counter, ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
        this.counter = counter;
    }

    @Autowired
    public TrackCommand(MeterRegistry meterRegistry) {
        counter = Counter
            .builder("Telegram_messages_handled_total")
            .description("Counts how many times the /track command has been processed")
            .tags("command", command())
            .register(meterRegistry);
    }

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
    @SuppressWarnings("MagicNumber")
    public SendMessage handle(Update update) {
        counter.increment();
        // Start tracking link
        String url = update.message().text().substring(7);
        try {
            scrapperClient.postLinks(update.message().chat().id(), new LinkRequest(URI.create(url)));
        } catch (WebClientResponseException.BadRequest e) {
            return new SendMessage(update.message().chat().id(),
                "Sorry, this url is not supported now, you can add "
                    + "either github repository url or stackoverflow question url ");
        }
        return new SendMessage(update.message().chat().id(),
            """
                  New url for tracking is added to a tracking list!
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
        counter.increment();
        // Taking message in accordance with exit code
        String message = commandErrorCode.getCommandErrorCode(exitCode);
        return new SendMessage(update.message().chat().id(), message);
    }
}
