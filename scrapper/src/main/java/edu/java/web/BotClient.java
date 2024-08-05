package edu.java.web;

import edu.java.responseDTO.BotRequest;
import org.springframework.http.HttpStatusCode;

public interface BotClient {
    HttpStatusCode sendUpdate(BotRequest botRequest);
}
