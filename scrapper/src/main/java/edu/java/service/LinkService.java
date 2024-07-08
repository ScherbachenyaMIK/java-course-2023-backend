package edu.java.service;

import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    LinkDTO add(long tgChatId, URI url);

    LinkDTO remove(long tgChatId, URI url);

    List<LinkDTO> listAll(long tgChatId);

    List<LinkDTO> listAllByFilter();

    List<ChatDTO> listChatsForLink(Long linkId);

    void updateLink(Long linkId, OffsetDateTime lastUpdate, OffsetDateTime lastSeen);
}
