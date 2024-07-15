package edu.java.bot.retry;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class RetryExceptionChecker {
    @Autowired
    List<Integer> codesForRetry;

    public boolean checkCode(WebClientResponseException exception) {
        return codesForRetry.contains(exception.getStatusCode().value());
    }
}
