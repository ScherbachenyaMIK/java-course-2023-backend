package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrackCommandTest {
    TrackCommand trackCommand = new TrackCommand();

    @Test
    void supportsValidCommand() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/track");

        assertEquals("EXIT_CODE_2", trackCommand.supports(update));
    }

    @Test
    void supportsValidLink() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/track https://example.com");

        assertEquals("EXIT_CODE_0", trackCommand.supports(update));
    }

    @Test
    void supportsInvalidLink() {
        Update update = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.text()).thenReturn("/track not_a_valid_url");

        assertEquals("EXIT_CODE_1", trackCommand.supports(update));
    }


    @Test
    void handle() {
        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);

        SendMessage sendMessage = trackCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();
        String expectedMessage = """
                  Command is recognized

                  You enter command /track!
                  """;

        assertEquals(expectedMessage, actualMessage);
    }
}
