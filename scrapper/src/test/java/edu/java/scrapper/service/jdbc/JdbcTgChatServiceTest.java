package edu.java.scrapper.service.jdbc;

import edu.java.DB.jdbc.DAO.ChatDAO;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.exception.UserAlreadyRegisteredException;
import edu.java.service.jdbc.JdbcTgChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcTgChatServiceTest {

    @Mock
    private ChatDAO chatDAO;

    @InjectMocks
    private JdbcTgChatService jdbcTgChatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_SuccessfulRegistration() {
        long tgChatId = 123L;
        when(chatDAO.findChatId(tgChatId)).thenReturn(-1L);

        jdbcTgChatService.register(tgChatId);

        verify(chatDAO).addChat(tgChatId);
    }

    @Test
    void register_UserAlreadyRegisteredExceptionThrown() {
        long tgChatId = 123L;
        when(chatDAO.findChatId(tgChatId)).thenReturn(1L);

        assertThrows(UserAlreadyRegisteredException.class, () -> jdbcTgChatService.register(tgChatId));
    }

    @Test
    void unregister_SuccessfulUnregistration() {
        long tgChatId = 123L;
        when(chatDAO.findChatId(tgChatId)).thenReturn(1L);
        doNothing().when(chatDAO).removeChat(tgChatId);

        jdbcTgChatService.unregister(tgChatId);

        verify(chatDAO).removeChat(tgChatId);
    }

    @Test
    void unregister_NoSuchUserRegisteredExceptionThrown() {
        long tgChatId = 123L;
        when(chatDAO.findChatId(tgChatId)).thenReturn(-1L);

        assertThrows(NoSuchUserRegisteredException.class, () -> jdbcTgChatService.unregister(tgChatId));
    }
}
