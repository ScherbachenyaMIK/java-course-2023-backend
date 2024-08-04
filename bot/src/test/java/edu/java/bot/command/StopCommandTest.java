package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.web.ScrapperClient;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void supports() {
        MockitoAnnotations.openMocks(this);

        assertThat(stopCommand.supports(null)).isEqualTo("EXIT_CODE_0");
    }

    @Test
    void handleNotSupports() {
        MockitoAnnotations.openMocks(this);

        SendMessage expected = new SendMessage(1L,
            "Command is not supported");

        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(1L);

        assertThat(stopCommand.handleNotSupports(mockUpdate, null))
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
