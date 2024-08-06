package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.web.ScrapperClient;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StartCommandTest {
    @Mock
    ScrapperClient scrapperClient;
    @Mock
    Counter counter;
    @InjectMocks
    StartCommand startCommand;

    @Test
    void command() {
        MockitoAnnotations.openMocks(this);
        assertEquals("/start", startCommand.command());
    }

    @Test
    void description() {
        MockitoAnnotations.openMocks(this);
        assertEquals("Initiates the bots operation. "
            + "Upon entering this command, the bot starts its operation "
            + "and is ready to accept further instructions.",
            startCommand.description());

    }

    @Test
    void handle() {
        MockitoAnnotations.openMocks(this);

        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(1L);
        when(scrapperClient.postTgChatId(1L)).thenReturn(HttpStatus.OK);

        SendMessage sendMessage = startCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();
        String expectedMessage = """
            Bot launched!

            Use the command /track to add the websites you're interested in.
            """;

        assertEquals(expectedMessage, actualMessage);
    }
}
