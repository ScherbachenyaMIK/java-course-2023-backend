package edu.java.bot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramExceptionLogger {
    private static final Logger LOGGER_RESPONSE = LoggerFactory.getLogger(TelegramExceptionLogger.class);

    public void logRequest(int errorCode, String description) {
        LOGGER_RESPONSE.trace("Error! Error code was: {}, Description: {}",
            errorCode, description);
    }
}
