package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.web.ScrapperClient;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StopCommandTest {

    @Mock
    ScrapperClient scrapperClient;
    @InjectMocks
    StopCommand stopCommand;

    @Test
    void command() {
        MockitoAnnotations.openMocks(this);
        assertEquals("close", stopCommand.command());
    }

    @Test
    void handle() {
        MockitoAnnotations.openMocks(this);

        Update mockUpdate = mock(Update.class);
        ChatMemberUpdated mockMyChatMember = mock(ChatMemberUpdated.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.myChatMember()).thenReturn(mockMyChatMember);
        when(mockMyChatMember.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(1L);
        when(scrapperClient.deleteTgChatId(1L)).thenReturn(HttpStatus.OK);

        stopCommand.handle(mockUpdate);

        verify(scrapperClient, times(1)).deleteTgChatId(1L);
    }
}
