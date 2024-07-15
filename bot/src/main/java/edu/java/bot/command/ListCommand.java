package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.responseDTO.LinkResponse;
import edu.java.bot.model.responseDTO.ListLinksResponse;
import edu.java.bot.web.ScrapperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
public class ListCommand implements FunctionalCommand {
    @Autowired
    private ScrapperClient scrapperClient;

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
