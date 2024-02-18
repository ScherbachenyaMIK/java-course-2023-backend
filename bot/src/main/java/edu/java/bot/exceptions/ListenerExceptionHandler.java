package edu.java.bot.exceptions;

import com.pengrad.telegrambot.TelegramException;
import edu.java.bot.util.NullResponseExceptionLogger;
import edu.java.bot.util.TelegramExceptionLogger;

@SuppressWarnings("HideUtilityClassConstructor")
public class ListenerExceptionHandler {
     public static void exceptionHandling(TelegramException exception) {
        if (exception.response() != null) {
            // Got bad response from telegram
            new TelegramExceptionLogger().logRequest(
                exception.response().errorCode(),
                exception.response().description());
        } else {
            // Probably network error
            new NullResponseExceptionLogger().logRequest();
        }
    }
}
