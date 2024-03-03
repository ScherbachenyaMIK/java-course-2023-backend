package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UntrackCommandTest {
    UntrackCommand untrackCommand = new UntrackCommand();
    @Test
    void command() {
        assertEquals("/untrack", untrackCommand.command());
    }

    @Test
    void description() {
        assertEquals("Removes a website from the list of tracked websites. "
            + "Upon entering this command, the bot allows you to remove a website "
            + "from the list of tracked websites. You will need to specify the URL "
            + "of the website you want to remove from the list. Example usage: /untrack [URL].",
            untrackCommand.description());
    }

    @Test
    void handle() {
        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);

        SendMessage sendMessage = untrackCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();
        String expectedMessage = """
                  Command is recognized

                  You enter command /untrack!
                  """;

        assertEquals(expectedMessage, actualMessage);
    }
}
