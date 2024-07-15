package edu.java.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
        maxAttemptsExpression = "#{${spring.retry-config.exponential-backoff-retry.max-attempts-expression}}",
        exceptionExpression = "@retryExceptionChecker.checkCode(#root)",
        backoff = @Backoff(delayExpression = "#{${spring.retry-config.exponential-backoff-retry.delay-expression}}",
                maxDelayExpression = "#{${spring.retry-config.exponential-backoff-retry.max-delay-expression}}",
                multiplierExpression = "#{${spring.retry-config.exponential-backoff-retry.multiplier-expression}}"
        ))
public @interface ExponentialBackoffRetry {
    @AliasFor(annotation = Retryable.class, attribute = "recover")
    String recover() default "";

    @AliasFor(annotation = Retryable.class, attribute = "value")
    Class<? extends Throwable>[] value() default {};

    @AliasFor(annotation = Retryable.class, attribute = "include")
    Class<? extends Throwable>[] include() default {};

    @AliasFor(annotation = Retryable.class, attribute = "exclude")
    Class<? extends Throwable>[] exclude() default {};

    @AliasFor(annotation = Retryable.class, attribute = "label")
    String label() default "";
}
