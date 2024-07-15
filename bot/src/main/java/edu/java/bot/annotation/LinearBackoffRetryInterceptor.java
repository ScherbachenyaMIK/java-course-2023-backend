package edu.java.bot.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LinearBackoffRetryInterceptor {
    @Autowired
    RetryTemplate retryTemplate;

    @Around("@annotation(edu.java.bot.annotation.LinearBackoffRetry)")
    public Object interceptMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Class<?>[] parameterTypes = Arrays.stream(joinPoint.getArgs())
            .map(Object::getClass)
            .toArray(Class[]::new);
        Method method = targetClass.getMethod(methodName, parameterTypes);
        return retryTemplate.execute(context -> {
            Object result;
            try {
                result = method.invoke(joinPoint.getTarget(), joinPoint.getArgs());
            } catch (InvocationTargetException exception) {
                throw (Exception) exception.getTargetException();
            }
            return result;
        });
    }
}
