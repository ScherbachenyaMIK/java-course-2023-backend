package edu.java.web;

import edu.java.DB.jdbc.DTO.LinkDTO;
import edu.java.responseDTO.BotRequest;
import edu.java.responseDTO.GitHubResponse;
import edu.java.responseDTO.StackOverflowResponse;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.jdbc.JdbcLinkService;
import java.lang.reflect.Field;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinkUpdaterSchedulerTest extends IntegrationTest {
    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverflowClient stackOverflowClient;

    @Mock
    private JdbcLinkService jdbcLinkService;

    @Mock
    private BotClient botClient;

    @InjectMocks
    private LinkUpdaterScheduler linkUpdaterScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void update_LinkFromGitHub_NewUpdatesSent() throws NoSuchFieldException, IllegalAccessException {
        LinkDTO githubLink = new LinkDTO(
            1L,
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            OffsetDateTime.now().minusMinutes(2),
            OffsetDateTime.now().minusMinutes(2));
        when(jdbcLinkService.listAllByFilter()).thenReturn(Collections.singletonList(githubLink));
        when(gitHubClient.getResponse(anyString(), anyString()))
            .thenReturn(new GitHubResponse("1", OffsetDateTime.now().minusMinutes(1)));

        linkUpdaterScheduler.update();

        Field field = linkUpdaterScheduler.getClass().getDeclaredField("botClient");
        field.setAccessible(true);
        BotClient botClient = (BotClient) field.get(linkUpdaterScheduler);
        verify(botClient, times(1)).sendUpdate(any(BotRequest.class));
    }

    @Test
    void update_LinkFromStackOverflow_NewUpdatesSent() throws NoSuchFieldException, IllegalAccessException {
        LinkDTO stackOverflowLink = new LinkDTO(
            2L,
            URI.create("https://api.stackexchange.com/2.2/questions" +
                "/12345?order=desc&sort=activity&site=stackoverflow"),
            OffsetDateTime.now().minusMinutes(2),
            OffsetDateTime.now().minusMinutes(2));
        when(jdbcLinkService.listAllByFilter()).thenReturn(Collections.singletonList(stackOverflowLink));
        when(stackOverflowClient.getResponse(anyString())).thenReturn(new StackOverflowResponse("12345", OffsetDateTime.now().minusMinutes(2)));

        linkUpdaterScheduler.update();

        Field field = linkUpdaterScheduler.getClass().getDeclaredField("botClient");
        field.setAccessible(true);
        BotClient botClient = (BotClient) field.get(linkUpdaterScheduler);
        verify(botClient, times(1)).sendUpdate(any(BotRequest.class));
    }
}

