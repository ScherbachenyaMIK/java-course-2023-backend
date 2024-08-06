package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.requestDTO.LinkRequest;
import edu.java.bot.web.ScrapperClient;
import io.micrometer.core.instrument.Counter;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UntrackCommandTest {
    @Mock
    ScrapperClient scrapperClient;
    @Mock
    Counter counter;
    @InjectMocks
    UntrackCommand untrackCommand;

    @Test
    void command() {
        MockitoAnnotations.openMocks(this);
        assertEquals("/untrack", untrackCommand.command());
    }

    @Test
    void description() {
        MockitoAnnotations.openMocks(this);
        assertEquals("Removes a website from the list of tracked websites. "
            + "Upon entering this command, the bot allows you to remove a website "
            + "from the list of tracked websites. You will need to specify the URL "
            + "of the website you want to remove from the list. Example usage: /untrack [URL].",
            untrackCommand.description());
    }

    @Test
    void handle() {
        MockitoAnnotations.openMocks(this);
        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockUpdate.message().text())
            .thenReturn("/untrack https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(1L);
        when(scrapperClient.deleteLinks(
            1L,
            new LinkRequest(
                URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"))))
            .thenReturn(HttpStatus.OK);

        SendMessage sendMessage = untrackCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();
        String expectedMessage = """
                  The bot will no longer track this url
                  """;

        assertEquals(expectedMessage, actualMessage);
    }
}
