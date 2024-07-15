package edu.java.bot.configuration;

import edu.java.bot.retry.LinearRetryTemplateBuilder;
import edu.java.bot.retry.RetryExceptionChecker;
import edu.java.bot.retry.backoff.LinearBackOffPolicy;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@EnableAspectJAutoProxy
@Configuration
@ConfigurationProperties(prefix = "spring.retry-config.linear-backoff-retry")
@Data
public class LinearBackoffRetryConfiguration {
    private int maxAttempts;

    private long delay;

    private long maxDelay;

    private long delayScale;

    @Autowired
    RetryExceptionChecker retryExceptionChecker;

    @Bean
    public RetryTemplate retryTemplate() throws NoSuchMethodException {
        LinearBackOffPolicy linearBackOffPolicy = new LinearBackOffPolicy();
        linearBackOffPolicy.setInitialInterval(delay);
        linearBackOffPolicy.setMaxInterval(maxDelay);
        linearBackOffPolicy.setScale(delayScale);

        LinearRetryTemplateBuilder builder = LinearRetryTemplateBuilder.newBuilder();
        builder.setMaxAttempts(maxAttempts);
        builder.setLinearBackOffPolicy(linearBackOffPolicy);
        builder.setPolicyMethod(Pair.of(
                retryExceptionChecker,
                retryExceptionChecker.getClass().getMethod("checkCode", WebClientResponseException.class
                )));

        return builder.build();
    }
}
