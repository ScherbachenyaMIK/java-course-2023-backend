package edu.java.scrapper.service.jpa;

import edu.java.DB.jpa.model.Chat;
import edu.java.DB.jpa.model.Link;
import edu.java.DB.jpa.repository.ChatRepository;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.exception.UserAlreadyRegisteredException;
import edu.java.service.jpa.JpaTgChatService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaTgChatServiceTest {
    @Mock
    ChatRepository chatRepository;

    @InjectMocks
    JpaTgChatService jpaTgChatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_SuccessfulRegistration() {
        long tgChatId = 123L;
        when(chatRepository.findIdByChatId(tgChatId)).thenReturn(null);

        jpaTgChatService.register(tgChatId);

        verify(chatRepository).save(any());
    }

    @Test
    void register_UserAlreadyRegisteredExceptionThrown() {
        long tgChatId = 123L;
        when(chatRepository.findIdByChatId(tgChatId)).thenReturn(1L);

        assertThrows(UserAlreadyRegisteredException.class, () -> jpaTgChatService.register(tgChatId));
    }

    @Test
    void unregister_SuccessfulUnregistration() {
        long tgChatId = 123L;
        long id = 1L;
        Chat chat = new Chat();
        chat.setChatId(tgChatId);
        chat.setId(id);
        List<Link> links = new ArrayList<>();
        Link link = new Link();
        link.addChat(chat);
        links.add(link);
        links.add(link);
        links.add(link);
        when(chatRepository.findIdByChatId(tgChatId)).thenReturn(id);
        when(chatRepository.findChatByChatId(tgChatId)).thenReturn(chat);
        when(chatRepository.findAllLinksForChat(tgChatId)).thenReturn(links);
        doNothing().when(chatRepository).removeByChatId(tgChatId);

        jpaTgChatService.unregister(tgChatId);

        verify(chatRepository).removeByChatId(tgChatId);
    }

    @Test
    void unregister_NoSuchUserRegisteredExceptionThrown() {
        long tgChatId = 123L;
        when(chatRepository.findIdByChatId(tgChatId)).thenReturn(null);

        assertThrows(NoSuchUserRegisteredException.class, () -> jpaTgChatService.unregister(tgChatId));
    }
}
