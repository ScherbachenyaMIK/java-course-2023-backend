package edu.java.bot.controller;

import edu.java.bot.model.requestDTO.LinkUpdateRequest;
import edu.java.bot.service.MessageSender;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ListenerTest {
    @Mock
    MessageSender messageSender;

    @InjectMocks
    Listener listener;

    @Test
    public void sendUpdate() {
        MockitoAnnotations.openMocks(this);

        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            1L,
            URI.create("http://github.com/"),
            "test",
            List.of(1L, 2L, 3L)
        );

        listener.listen(null);

        verify(messageSender, times(1)).sendMessage(null);
    }
}
