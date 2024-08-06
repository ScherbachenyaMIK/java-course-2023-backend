package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class HelpCommand implements BaseCommand {
    @Autowired
    FunctionalCommandList functionalCommandList;

    private final Counter counter;

    public HelpCommand() {
        counter = null;
    }

    @Autowired
    public HelpCommand(MeterRegistry meterRegistry) {
        counter = Counter
            .builder("Telegram_messages_handled_total")
            .description("Counts how many times the /help command has been processed")
            .tags("command", command())
            .register(meterRegistry);
    }

    @Override
    public String command() {
        return "/help";
    }

    public String description() {
        return """
            The /help command has been entered.
            This command prints information about all currently supported bot commands.

            List of available commands:
            """;
    }

    @Override
    public SendMessage handle(Update update) {
        counter.increment();
        // Writing of list of commands
        List<FunctionalCommand> commands = functionalCommandList.getFunctionalCommandList();
        StringBuilder stringBuilder = new StringBuilder(description());
        for (var i : commands) {
            stringBuilder.append(i.command())
                .append(" - ")
                .append(i.description())
                .append("\n");
        }
        return new SendMessage(update.message().chat().id(),
            stringBuilder.toString());
    }
}
