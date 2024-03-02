package edu.java.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LinkUpdaterScheduler {
    @Autowired
    private GitHubClient gitHubClient;

    @Autowired
    private StackOverflowClient stackOverflowClient;

    @SuppressWarnings("MultipleStringLiterals")
    @Scheduled(fixedDelayString = "#{T(java.util.concurrent.TimeUnit).SECONDS.toMillis('${app.scheduler.interval}')}")
    public void update() {
        log.info("{} executing task...", gitHubClient.getClass().getSimpleName());
        log.info("{} executing task...", stackOverflowClient.getClass().getSimpleName());
    }
}
