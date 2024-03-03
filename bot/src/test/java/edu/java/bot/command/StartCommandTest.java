package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StartCommandTest {
    StartCommand startCommand = new StartCommand();

    @Test
    void command() {
        assertEquals("/start", startCommand.command());
    }

    @Test
    void description() {
        assertEquals("Initiates the bots operation. "
            + "Upon entering this command, the bot starts its operation "
            + "and is ready to accept further instructions.",
            startCommand.description());

    }

    @Test
    void handle() {
        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);

        SendMessage sendMessage = startCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();
        String expectedMessage = """
            Bot launched!

            Use the command /track to add the websites you're interested in.
            """;

        assertEquals(expectedMessage, actualMessage);
    }
}
