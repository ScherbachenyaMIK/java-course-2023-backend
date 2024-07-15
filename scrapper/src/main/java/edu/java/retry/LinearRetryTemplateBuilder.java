package edu.java.retry;

import edu.java.retry.backoff.LinearBackOffPolicy;
import edu.java.retry.policy.MethodRetryPolicy;
import java.lang.reflect.Method;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.retry.support.RetryTemplate;

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
        return RetryTemplate.builder()
            .customBackoff(linearBackOffPolicy)
            .customPolicy(new MethodRetryPolicy(maxAttempts, policyMethod))
            .build();
    }
}
