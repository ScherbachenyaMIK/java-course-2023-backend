package edu.java.web;

import edu.java.responseDTO.BotRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

    @Autowired
    private BotClient botClient;

    @SuppressWarnings("MultipleStringLiterals")
    @Scheduled(fixedDelayString = "#{T(java.util.concurrent.TimeUnit).SECONDS.toMillis('${app.scheduler.interval}')}")
    public void update() {
        log.info("{} executing task...", gitHubClient.getClass().getSimpleName());
        log.info("{} executing task...", stackOverflowClient.getClass().getSimpleName());
        try {
            ArrayList<Long> ids = new ArrayList<>();
            ids.add(1L);
            botClient.sendUpdate(new BotRequest(1L,
                new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
                "description",
                ids));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
