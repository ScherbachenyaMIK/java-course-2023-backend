package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.responseDTO.LinkResponse;
import edu.java.bot.model.responseDTO.ListLinksResponse;
import edu.java.bot.web.ScrapperClient;
import io.micrometer.core.instrument.Counter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListCommandTest {
    @Mock
    ScrapperClient scrapperClient;
    @Mock
    Counter counter;
    @InjectMocks
    ListCommand listCommand;

    @Test
    void command() {
        MockitoAnnotations.openMocks(this);
        assertEquals("/list", listCommand.command());
    }

    @Test
    void description() {
        MockitoAnnotations.openMocks(this);
        assertEquals("Displays the list of tracked website URLs. "
            + "Upon entering this command, the bot shows a list of all the "
            + "URLs of websites currently being tracked.",
            listCommand.description());
    }

    @Test
    void handle() {
        MockitoAnnotations.openMocks(this);

        List<LinkResponse> listResponse = new ArrayList<>();
        listResponse.add(new LinkResponse(1L,
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend")));
        listResponse.add(new LinkResponse(1L,
            URI.create("https://api.stackexchange.com/2.2/questions"
                + "/123456?order=desc&sort=activity&site=stackoverflow")));
        ListLinksResponse response = new ListLinksResponse(listResponse, listResponse.size());

        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.message()).thenReturn(mockMessage);
        when(mockMessage.chat()).thenReturn(mockChat);
        when(mockChat.id()).thenReturn(1L);
        when(scrapperClient.getLinks(1L)).thenReturn(response);

        SendMessage sendMessage = listCommand.handle(mockUpdate);
        String actualMessage = sendMessage.getParameters().get("text").toString();
        String expectedMessage = """
                  List of tracked links:

                  https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend;
                  https://api.stackexchange.com/2.2/questions/123456?order=desc&sort=activity&site=stackoverflow;
                  """;

        assertEquals(expectedMessage, actualMessage);
    }
}
