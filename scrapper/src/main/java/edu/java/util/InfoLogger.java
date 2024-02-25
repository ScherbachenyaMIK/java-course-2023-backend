package edu.java.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InfoLogger {
    private static final Logger LOGGER_RESPONSE = LoggerFactory.getLogger(InfoLogger.class);

    public void logRequest(Class<?> parameterClass) {
        LOGGER_RESPONSE.trace("Client {} start making requests",
            parameterClass.getSimpleName());
    }
}
