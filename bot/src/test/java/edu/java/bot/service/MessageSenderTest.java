package edu.java.bot.service;

import edu.java.bot.model.requestDTO.LinkUpdateRequest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MessageSenderTest {
    @Mock
    CustomTelegramBot telegramBot;

    @InjectMocks
    MessageSender messageSender;

    @Test
    public void sendMessage() {
        MockitoAnnotations.openMocks(this);

        int listSize = 5;
        List<Long> tgChatsIds = List.of(1L, 2L, 3L, 4L, 5L);
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            1L,
            URI.create("http://github.com/"),
            "test",
            tgChatsIds
        );

        messageSender.sendMessage(linkUpdateRequest);

        verify(telegramBot, times(listSize)).execute(any());
    }
}
