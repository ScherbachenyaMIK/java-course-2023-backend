package edu.java.service.jpa;

import edu.java.DB.jpa.model.Chat;
import edu.java.DB.jpa.repository.ChatRepository;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.exception.UserAlreadyRegisteredException;
import edu.java.service.TgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class JpaTgChatService implements TgChatService {
    @Autowired
    ChatRepository chatRepository;

    @Transactional
    @Override
    public void register(long tgChatId) {
        Long chatID = chatRepository.findIdByChatId(tgChatId);
        if (chatID != null) {
            throw new UserAlreadyRegisteredException();
        }
        this.addChatByChatId(tgChatId);
    }

    @Transactional
    @Override
    public void unregister(long tgChatId) {
        Long chatId = chatRepository.findIdByChatId(tgChatId);
        if (chatId == null) {
            throw new NoSuchUserRegisteredException();
        }
        Chat chat = chatRepository.findChatByChatId(tgChatId);
        chatRepository.findAllLinksForChat(tgChatId)
            .forEach(item -> {
                item.removeChat(chat);
            });
        chatRepository.flush();

        chatRepository.removeByChatId(tgChatId);
    }

    private void addChatByChatId(long tgChatId) {
        Chat chat = new Chat();
        chat.setChatId(tgChatId);
        chatRepository.save(chat);
    }
}
