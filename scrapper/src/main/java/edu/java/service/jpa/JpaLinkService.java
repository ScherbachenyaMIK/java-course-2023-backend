package edu.java.service.jpa;

import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import edu.java.DB.jpa.model.Chat;
import edu.java.DB.jpa.model.Link;
import edu.java.DB.jpa.repository.ChatRepository;
import edu.java.DB.jpa.repository.LinkRepository;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.responseDTO.GitHubResponse;
import edu.java.responseDTO.StackOverflowResponse;
import edu.java.service.LinkService;
import edu.java.util.ChatEntityDTOConverter;
import edu.java.util.LinkEntityDTOConverter;
import edu.java.util.LinkParser;
import edu.java.web.GitHubClient;
import edu.java.web.StackOverflowClient;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class JpaLinkService implements LinkService {
    @Autowired
    LinkRepository linkRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    GitHubClient gitHubClient;
    @Autowired
    StackOverflowClient stackOverflowClient;

    @Transactional
    @Override
    @SuppressWarnings("MagicNumber")
    public LinkDTO add(long tgChatId, URI url) {
        Chat chat = chatRepository.findChatByChatId(tgChatId);
        if (chat == null) {
            throw new NoSuchUserRegisteredException();
        }
        Link result = linkRepository.findLinkByLink(url.toString());
        if (result != null) {
            result.addChat(chat);
            return LinkEntityDTOConverter.convert(result);
        }
        if (LinkParser.parseLink(url).equals("Github")) {
            String[] parts = url.toString().split("/");
            GitHubResponse gitHubResponse = gitHubClient.getResponse(parts[4], parts[5]);
            this.addLinkByLinkAndLastUpdate(
                url.toString(),
                Timestamp.from(gitHubResponse.updatedAt().toInstant())
            );
        } else {
            String[] parts = url.toString().split("/");
            StackOverflowResponse stackOverflowResponse = stackOverflowClient.getResponse(
                parts[5].substring(0, parts[5].indexOf('?')));
            this.addLinkByLinkAndLastUpdate(
                url.toString(),
                Timestamp.from(stackOverflowResponse.lastActivityDate().toInstant())
            );
        }
        result = linkRepository.findLinkByLink(url.toString());
        result.addChat(chat);
        return LinkEntityDTOConverter.convert(result);
    }

    @Transactional
    @Override
    public LinkDTO remove(long tgChatId, URI url) {
        Chat chat = chatRepository.findChatByChatId(tgChatId);
        if (chat == null) {
            throw new NoSuchUserRegisteredException();
        }
        Link result = linkRepository.findLinkByLink(url.toString());
        if (result == null) {
            throw new LinkNotFoundException();
        }
        result.removeChat(chat);
        return LinkEntityDTOConverter.convert(result);
    }

    @Transactional
    @Override
    public List<LinkDTO> listAll(long tgChatId) {
        Long chatID = chatRepository.findIdByChatId(tgChatId);
        if (chatID == null) {
            throw new NoSuchUserRegisteredException();
        }
        return chatRepository
            .findAllLinksForChat(tgChatId)
            .stream()
            .map(LinkEntityDTOConverter::convert)
            .collect(Collectors.toList());
    }

    @Override
    public List<LinkDTO> listAllByFilter() {
        return linkRepository.findAllLinksWithFilter()
            .stream()
            .map(LinkEntityDTOConverter::convert)
            .collect(Collectors.toList());
    }

    @Override
    public List<ChatDTO> listChatsForLink(Long linkId) {
        return linkRepository.findAllChatsForLink(linkId)
            .stream()
            .map(ChatEntityDTOConverter::convert)
            .collect(Collectors.toList());
    }

    @Override
    public void updateLink(Long linkId, OffsetDateTime lastUpdate, OffsetDateTime lastSeen) {
        Link link = linkRepository.findLinkById(linkId);
        link.setLastUpdate(Timestamp.from(lastUpdate.toInstant()));
        link.setLastSeen(Timestamp.from(lastSeen.toInstant()));
        linkRepository.saveAndFlush(link);
    }

    private void addLinkByLinkAndLastUpdate(String link, Timestamp lastUpdate) {
        Link linkEntity = new Link();
        linkEntity.setLink(link);
        linkEntity.setLastUpdate(lastUpdate);
        linkRepository.save(linkEntity);
    }
}
