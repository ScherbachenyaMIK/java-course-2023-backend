package edu.java.service.jdbc;

import edu.java.DB.jdbc.DAO.ChatDAO;
import edu.java.DB.jdbc.DAO.ChatLinkDAO;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.exception.UserAlreadyRegisteredException;
import edu.java.service.TgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class JdbcTgChatService implements TgChatService {
    @Autowired
    ChatDAO chatDAO;

    @Autowired
    ChatLinkDAO chatLinkDAO;

    @Transactional
    @Override
    public void register(long tgChatId) {
        Long chatID = chatDAO.findChatId(tgChatId);
        if (chatID != -1) {
            throw new UserAlreadyRegisteredException();
        }
        chatDAO.addChat(tgChatId);
    }

    @Transactional
    @Override
    public void unregister(long tgChatId) {
        Long chatID = chatDAO.findChatId(tgChatId);
        if (chatID == -1) {
            throw new NoSuchUserRegisteredException();
        }
        chatDAO.findAllLinksForChat(tgChatId)
            .forEach(item -> chatLinkDAO.removeChatLink(chatID, item.id()));
        chatDAO.removeChat(tgChatId);
    }
}
