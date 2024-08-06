package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.web.ScrapperClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 5)
public class StopCommand implements BaseCommand {
    @Autowired
    private ScrapperClient scrapperClient;

    private final Counter counter;

    public StopCommand(Counter counter, ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
        this.counter = counter;
    }

    @Autowired
    public StopCommand(MeterRegistry meterRegistry) {
        counter = Counter
            .builder("Telegram_messages_handled_total")
            .description("Counts how many times the /stop command has been processed")
            .tags("command", "/stop")
            .register(meterRegistry);
    }

    @Override
    public String command() {
        return "close";
    }

    @Override
    public SendMessage handle(Update update) {
        counter.increment();
        scrapperClient.deleteTgChatId(update.myChatMember().chat().id());
        return null;
    }
}
