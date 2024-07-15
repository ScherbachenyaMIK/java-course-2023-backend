package edu.java.bot.retry;

import edu.java.bot.retry.backoff.LinearBackOffPolicy;
import edu.java.bot.retry.policy.MethodRetryPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@SuppressWarnings("MagicNumber")
public class LinearRetryTemplateBuilder {
    @Setter
    private int maxAttempts = 3;

    @Setter
    private LinearBackOffPolicy linearBackOffPolicy = new LinearBackOffPolicy();

    @Setter
    private Pair<Object, Method> policyMethod;

    private LinearRetryTemplateBuilder() {
    }

    public static LinearRetryTemplateBuilder newBuilder() {
        return new LinearRetryTemplateBuilder();
    }

    public RetryTemplate build() {
        RetryTemplateBuilder retryTemplateBuilder = RetryTemplate.builder();
        Map<Class<? extends Throwable>, Boolean> map = new HashMap<>();
        map.put(WebClientResponseException.class, true);
        return retryTemplateBuilder
            .customBackoff(linearBackOffPolicy)
            .customPolicy(new MethodRetryPolicy(maxAttempts, policyMethod))
            .build();
    }
}
