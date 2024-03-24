package edu.java.web;

import edu.java.DB.DTO.ChatDTO;
import edu.java.DB.DTO.LinkDTO;
import edu.java.responseDTO.BotRequest;
import edu.java.responseDTO.GitHubResponse;
import edu.java.responseDTO.StackOverflowResponse;
import edu.java.service.LinkService;
import edu.java.util.LinkParser;
import java.time.OffsetDateTime;
import java.util.List;
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

    @Autowired
    private LinkService linkService;

    @SuppressWarnings({"MultipleStringLiterals", "MagicNumber"})
    @Scheduled(fixedDelayString = "#{T(java.util.concurrent.TimeUnit).SECONDS.toMillis('${app.scheduler.interval}')}")
    public void update() {
        List<LinkDTO> links = linkService.listAllByFilter();
        for (var link : links) {
            if (LinkParser.parseLink(link.url()).equals("Github")) {
                String[] parts = link.url().toString().split("/");
                GitHubResponse gitHubResponse = gitHubClient.getResponse(parts[4], parts[5]);
                if (link.lastUpdate().isBefore(gitHubResponse.updatedAt())) {
                    botClient.sendUpdate(new BotRequest(link.id(), link.url(),
                        "There are new updates on this link",
                        linkService.listChatsForLink(link.id()).stream()
                            .map(ChatDTO::id)
                            .toList()));
                }
                linkService.updateLink(link.id(),
                    gitHubResponse.updatedAt(),
                    OffsetDateTime.now());
            } else {
                String[] parts = link.url().toString().split("/");
                StackOverflowResponse stackOverflowResponse = stackOverflowClient.getResponse(
                    parts[5].substring(0, parts[5].indexOf('?')));
                if (link.lastUpdate().isBefore(stackOverflowResponse.lastActivityDate())) {
                    botClient.sendUpdate(new BotRequest(link.id(), link.url(),
                        "There are new updates on this link",
                        linkService.listChatsForLink(link.id()).stream()
                            .map(ChatDTO::id)
                            .toList()));
                }
                linkService.updateLink(link.id(),
                    stackOverflowResponse.lastActivityDate(),
                    OffsetDateTime.now());
            }
        }
    }
}
