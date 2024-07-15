package edu.java.bot.retry.policy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.util.Assert;

public class MethodRetryPolicy extends SimpleRetryPolicy {
    private final Object object;
    private final Method method;

    public MethodRetryPolicy(int maxAttempts, Pair<Object, Method> method) {
        super(maxAttempts);
        Assert.notNull(method, "'method' cannot be null");
        this.object = method.getLeft();
        this.method = method.getRight();
    }

    public boolean canRetry(RetryContext context) {
        Throwable lastThrowable = context.getLastThrowable();
        if (lastThrowable == null) {
            return super.canRetry(context);
        } else {
            try {
                return super.canRetry(context) && (boolean) method.invoke(object, lastThrowable);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
