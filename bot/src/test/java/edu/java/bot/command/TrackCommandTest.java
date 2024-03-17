package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.requestDTO.LinkRequest;
import edu.java.bot.web.ScrapperClient;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrackCommandTest {
    @Mock
    ScrapperClient scrapperClient;
    @InjectMocks
    TrackCommand trackCommand;

    @Test
    void supportsValidCommand() {
        MockitoAnnotations.openMocks(this);
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/track");

        assertEquals("EXIT_CODE_2", trackCommand.supports(update));
    }

    @Test
    void supportsValidLink() {
        MockitoAnnotations.openMocks(this);
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/track https://example.com");

        assertEquals("EXIT_CODE_0", trackCommand.supports(update));
    }

    @Test
    void supportsInvalidLink() {
        MockitoAnnotations.openMocks(this);
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/track not_a_valid_url");

        assertEquals("EXIT_CODE_1", trackCommand.supports(update));
    }


    @Test
    void handle() {
        MockitoAnnotations.openMocks(this);
        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockUpdate.message().text())
            .thenReturn("/track https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(1L);
        when(scrapperClient.postLinks(
            1L,
            new LinkRequest(
                URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"))))
            .thenReturn(HttpStatus.OK);

        SendMessage sendMessage = trackCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();
        String expectedMessage = """
                  New url for tracking is added to a tracking list!
                  """;

        assertEquals(expectedMessage, actualMessage);
    }
}
