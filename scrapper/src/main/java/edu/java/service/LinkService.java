package edu.java.service;

import edu.java.DB.DTO.ChatDTO;
import edu.java.DB.DTO.LinkDTO;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface LinkService {
    LinkDTO add(long tgChatId, URI url);

    LinkDTO remove(long tgChatId, URI url);

    List<LinkDTO> listAll(long tgChatId);

    List<LinkDTO> listAllByFilter();

    List<ChatDTO> listChatsForLink(Long linkId);

    @Transactional void updateLink(Long linkId, OffsetDateTime lastUpdate, OffsetDateTime lastSeen);
}
