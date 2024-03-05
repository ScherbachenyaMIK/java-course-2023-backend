package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnknownCommandTest {

    @Test
    public void testCommand() {
        UnknownCommand unknownCommand = new UnknownCommand();
        assertEquals("/", unknownCommand.command());
    }

    @Test
    public void testHandle() {
        UnknownCommand unknownCommand = new UnknownCommand();

        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);
        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);

        SendMessage sendMessage = unknownCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();

        assertEquals(
            """
                Unknown command entered

                If you want to see a list of commands, enter /help.
                """,
            actualMessage);
    }
}
