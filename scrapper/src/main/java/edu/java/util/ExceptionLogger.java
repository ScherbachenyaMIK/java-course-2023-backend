package edu.java.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExceptionLogger {
    private static final Logger LOGGER_RESPONSE = LoggerFactory.getLogger(ExceptionLogger.class);

    public void logRequest(String errorCode, String description) {
        LOGGER_RESPONSE.trace("Error! Error code was: {}, Description: {}",
            errorCode, description);
    }
}
