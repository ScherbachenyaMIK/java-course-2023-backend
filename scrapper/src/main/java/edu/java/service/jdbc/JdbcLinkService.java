package edu.java.service.jdbc;

import edu.java.DB.DAO.ChatDAO;
import edu.java.DB.DAO.ChatLinkDAO;
import edu.java.DB.DAO.LinkDAO;
import edu.java.DB.DTO.ChatDTO;
import edu.java.DB.DTO.LinkDTO;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.responseDTO.GitHubResponse;
import edu.java.responseDTO.StackOverflowResponse;
import edu.java.service.LinkService;
import edu.java.util.LinkParser;
import edu.java.web.GitHubClient;
import edu.java.web.StackOverflowClient;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JdbcLinkService implements LinkService {
    @Autowired
    LinkDAO linkDAO;
    @Autowired
    ChatLinkDAO chatLinkDAO;
    @Autowired
    ChatDAO chatDAO;
    @Autowired
    GitHubClient gitHubClient;
    @Autowired
    StackOverflowClient stackOverflowClient;

    @Transactional
    @Override
    @SuppressWarnings("MagicNumber")
    public LinkDTO add(long tgChatId, URI url) {
        Long chatID = chatDAO.findChatId(tgChatId);
        if (chatID == -1L) {
            throw new NoSuchUserRegisteredException();
        }
        LinkDTO result = linkDAO.findLinkByURI(url);
        if (result.id() != -1L) {
            chatLinkDAO.addChatLink(chatDAO.findChatId(tgChatId), result.id());
            return result;
        }
        if (LinkParser.parseLink(url).equals("Github")) {
            String[] parts = url.toString().split("/");
            GitHubResponse gitHubResponse = gitHubClient.getResponse(parts[4], parts[5]);
            linkDAO.addLink(url, gitHubResponse.updatedAt());
        } else {
            String[] parts = url.toString().split("/");
            StackOverflowResponse stackOverflowResponse = stackOverflowClient.getResponse(
                parts[5].substring(0, parts[5].indexOf('?')));
            linkDAO.addLink(url, stackOverflowResponse.lastActivityDate());
        }
        result = linkDAO.findLinkByURI(url);
        chatLinkDAO.addChatLink(chatDAO.findChatId(tgChatId), result.id());
        return result;
    }

    @Transactional
    @Override
    public LinkDTO remove(long tgChatId, URI url) {
        Long chatID = chatDAO.findChatId(tgChatId);
        if (chatID == -1L) {
            throw new NoSuchUserRegisteredException();
        }
        LinkDTO result = linkDAO.findLinkByURI(url);
        if (result.id() == -1L) {
            throw new LinkNotFoundException();
        }
        chatLinkDAO.removeChatLink(chatDAO.findChatId(tgChatId), result.id());
        return result;
    }

    @Transactional
    @Override
    public List<LinkDTO> listAll(long tgChatId) {
        Long chatID = chatDAO.findChatId(tgChatId);
        if (chatID == -1L) {
            throw new NoSuchUserRegisteredException();
        }
        return chatDAO.findAllLinksForChat(tgChatId);
    }

    @Override
    public List<LinkDTO> listAllByFilter() {
        return linkDAO.findAllLinksWithFilter();
    }

    @Override
    public List<ChatDTO> listChatsForLink(Long linkId) {
        return linkDAO.findAllChatsForLink(linkId);
    }

    @Override
    public void updateLink(Long linkId, OffsetDateTime lastUpdate, OffsetDateTime lastSeen) {
        linkDAO.updateLink(linkId, lastUpdate, lastSeen);
    }
}
