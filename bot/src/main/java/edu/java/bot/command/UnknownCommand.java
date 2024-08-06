package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("DefaultAnnotationParam")
@Order(Ordered.LOWEST_PRECEDENCE)
public class UnknownCommand implements BaseCommand {
    private final Counter counter;

    public UnknownCommand(Counter counter) {
        this.counter = counter;
    }

    @Autowired
    public UnknownCommand(MeterRegistry meterRegistry) {
        counter = Counter
            .builder("Telegram_messages_handled_total")
            .description("Counts how many times the /unknown command has been processed")
            .tags("command", "/unknown")
            .register(meterRegistry);
    }

    @Override
    public String command() {
        return "/";
    }

    @Override
    public SendMessage handle(Update update) {
        counter.increment();
        // End tracking link
        return new SendMessage(update.message().chat().id(),
            """
                  Unknown command entered

                  If you want to see a list of commands, enter /help.
                  """);
    }
}
