package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.command.Command;
import edu.java.bot.command.FunctionalCommand;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.StopCommand;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.exception.ListenerExceptionHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomTelegramBot implements Bot {
    @Autowired
    private List<FunctionalCommand> commandList;

    @Autowired
    private StopCommand stopCommand;

    private final TelegramBot bot;

    @Autowired
    public CustomTelegramBot(ApplicationConfig applicationConfig) {
        this.bot = new TelegramBot(applicationConfig.telegramToken());
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        // Request execution
        bot.execute(request);
    }

    @Override
    public int process(List<Update> updates) {
        Command command;
        // Processing all of updates
        for (Update update : updates) {
            if (update.myChatMember() != null
                && update.myChatMember().newChatMember().user().username()
                .equals("url_notifications_tracking_bot")
                && update.myChatMember().newChatMember().status().name()
                .equals("kicked")) {
                stopCommand.handle(update);
            }
            if (update.message() != null) {
                command = MessageHandler.handleMessage(update.message());
                if (command != null) {
                    String exitCode = command.supports(update);
                    if (exitCode.equals("EXIT_CODE_0")) {
                        execute(command.handle(update));
                    } else {
                        execute(command.handleNotSupports(update, exitCode));
                    }
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void start() {
        // Register for updates
        setInlineKeyboard();
        bot.setUpdatesListener(this, ListenerExceptionHandler::exceptionHandling);
    }

    private void setInlineKeyboard() {
        BotCommand[] commands = new BotCommand[commandList.size() + 1];
        commands[0] = new BotCommand(new HelpCommand().command().substring(1),
            "Prints all supported command");
        for (int i = 1; i <= commandList.size(); ++i) {
            commands[i] = commandList.get(i - 1).toApiCommand();
        }
        SetMyCommands myCommands = new SetMyCommands(commands);
        execute(myCommands);
    }

    @Override
    public void close() {
        // Method for closing resources
    }
}
