package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.responseDTO.LinkResponse;
import edu.java.bot.model.responseDTO.ListLinksResponse;
import edu.java.bot.web.ScrapperClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
public class ListCommand implements FunctionalCommand {
    @Autowired
    private ScrapperClient scrapperClient;

    private final Counter counter;

    public ListCommand(Counter counter, ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
        this.counter = counter;
    }

    @Autowired
    public ListCommand(MeterRegistry meterRegistry) {
        counter = Counter
            .builder("Telegram_messages_handled_total")
            .description("Counts how many times the /list command has been processed")
            .tags("command", command())
            .register(meterRegistry);
    }

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
        counter.increment();
        // Writing of list of tracking links
        ListLinksResponse response = scrapperClient.getLinks(update.message().chat().id());
        if (response.size() == 0) {
            return new SendMessage(update.message().chat().id(),
                "You haven't added any links yet");
        }
        StringBuilder responseText = new StringBuilder("List of tracked links:\n\n");
        for (int i = 0; i < response.size(); ++i) {
            LinkResponse link = response.links().get(i);
            responseText.append(link.url()).append(";\n");
        }
        return new SendMessage(update.message().chat().id(),
            responseText.toString());
    }
}
