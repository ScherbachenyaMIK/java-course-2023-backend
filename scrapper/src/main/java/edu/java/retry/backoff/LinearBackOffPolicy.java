package edu.java.retry.backoff;

import lombok.Getter;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.Sleeper;
import org.springframework.retry.backoff.SleepingBackOffPolicy;
import org.springframework.retry.backoff.ThreadWaitSleeper;
import org.springframework.util.ClassUtils;

public class LinearBackOffPolicy implements SleepingBackOffPolicy<LinearBackOffPolicy> {

    public static final long DEFAULT_INITIAL_INTERVAL = 100L;

    public static final long DEFAULT_MAX_INTERVAL = 30000L;

    public static final long DEFAULT_SCALE = 2;

    @Getter
    private long initialInterval = DEFAULT_INITIAL_INTERVAL;

    @Getter
    private long maxInterval = DEFAULT_MAX_INTERVAL;

    @Getter
    private long scale = DEFAULT_SCALE;

    private Sleeper sleeper = new ThreadWaitSleeper();

    public void setSleeper(Sleeper sleeper) {
        this.sleeper = sleeper;
    }

    @Override
    public LinearBackOffPolicy withSleeper(Sleeper sleeper) {
        LinearBackOffPolicy res = newInstance();
        cloneValues(res);
        res.setSleeper(sleeper);
        return res;
    }

    protected LinearBackOffPolicy newInstance() {
        return new LinearBackOffPolicy();
    }

    protected void cloneValues(LinearBackOffPolicy target) {
        target.setInitialInterval(getInitialInterval());
        target.setMaxInterval(getMaxInterval());
        target.setScale(getScale());
        target.setSleeper(this.sleeper);
    }

    public void setInitialInterval(long initialInterval) {
        this.initialInterval = initialInterval > 1 ? initialInterval : 1;
    }

    public void setScale(long scale) {
        this.scale = scale > 1 ? scale : 1;
    }

    public void setMaxInterval(long maxInterval) {
        this.maxInterval = maxInterval > 0 ? maxInterval : 1;
    }

    @Override
    public BackOffContext start(RetryContext context) {
        return new LinearBackOffPolicy.LinearBackOffContext(this.initialInterval, this.scale, this.maxInterval);
    }

    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        LinearBackOffPolicy.LinearBackOffContext
            context = (LinearBackOffPolicy.LinearBackOffContext) backOffContext;
        try {
            long sleepTime = context.getSleepAndIncrement();
            this.sleeper.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
        }
    }

    @Override
    public String toString() {
        return ClassUtils.getShortName(getClass()) + "[initialInterval=" + getInitialInterval() + ", scale="
            + getScale() + ", maxInterval=" + getMaxInterval() + "]";
    }

    @Getter
    static class LinearBackOffContext implements BackOffContext {

        private final long scale;

        private long interval;

        private final long maxInterval;

        LinearBackOffContext(long interval, long scale, long maxInterval) {
            this.interval = interval;
            this.scale = scale;
            this.maxInterval = maxInterval;
        }

        public synchronized long getSleepAndIncrement() {
            long sleep = getInterval();
            long max = getMaxInterval();
            if (sleep > max) {
                sleep = max;
            } else {
                this.interval = getNextInterval();
            }
            return sleep;
        }

        protected long getNextInterval() {
            return (long) (this.interval + getScale());
        }
    }
}
