package edu.java.bot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullResponseExceptionLogger {
    private static final Logger LOGGER_RESPONSE = LoggerFactory.getLogger(TelegramExceptionLogger.class);

    public void logRequest() {
        LOGGER_RESPONSE.trace("Error! Telegram response was NULL");
    }
}

