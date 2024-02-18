package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListCommandTest {
    ListCommand listCommand = new ListCommand();

    @Test
    void command() {
        assertEquals("/list", listCommand.command());
    }

    @Test
    void description() {
        assertEquals("Displays the list of tracked website URLs. "
            + "Upon entering this command, the bot shows a list of all the "
            + "URLs of websites currently being tracked.",
            listCommand.description());
    }

    @Test
    void handle() {
        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);

        SendMessage sendMessage = listCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();
        String expectedMessage = """
                  Command is recognized

                  You enter command /list!
                  """;

        assertEquals(expectedMessage, actualMessage);
    }
}
