package edu.java.responseDTO;

import java.net.URI;
import java.util.List;

public record BotRequest(
    Long id,
    URI url,
    String description,
    List<Long> tgChatIds
) {}
