package edu.java.web;

import edu.java.util.InfoLogger;
import edu.java.util.ResponseHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdaterScheduler {

    @Autowired
    private List<WebSiteClient> webSiteClientList;

    @Autowired
    private InfoLogger infoLogger;

    @Scheduled(fixedDelayString = "#{T(java.util.concurrent.TimeUnit).SECONDS.toMillis('${app.scheduler.interval}')}")
    public void update() {
        for (var webSiteClient : webSiteClientList) {
            infoLogger.logRequest(webSiteClient.getClass());
            ResponseHandler.handleResponses(webSiteClient.getResponse());
        }
    }
}
